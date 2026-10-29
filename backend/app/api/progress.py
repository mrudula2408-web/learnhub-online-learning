from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.models import Course, Module, Enrollment, ModuleCompletion, User
from app.schemas.schemas import ModuleCompletionToggle, CourseProgressOut
from app.api.deps import get_current_user

router = APIRouter(prefix="/courses", tags=["Learning & Progress"])

@router.post("/{course_id}/modules/{module_id}/complete", response_model=CourseProgressOut)
def toggle_module_completion(
    course_id: int,
    module_id: int,
    toggle: ModuleCompletionToggle,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    course = db.query(Course).filter(Course.id == course_id).first()
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    module = db.query(Module).filter(Module.id == module_id, Module.course_id == course_id).first()
    if not module:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Module not found in this course")

    # Auto-enroll user if not already enrolled so students can start learning seamlessly
    enrollment = db.query(Enrollment).filter(
        Enrollment.user_id == current_user.id,
        Enrollment.course_id == course_id
    ).first()
    if not enrollment:
        enrollment = Enrollment(user_id=current_user.id, course_id=course_id, status="active")
        db.add(enrollment)
        db.flush()

    existing_completion = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.module_id == module_id
    ).first()

    if toggle.completed:
        if not existing_completion:
            completion = ModuleCompletion(
                user_id=current_user.id,
                module_id=module_id,
                course_id=course_id
            )
            db.add(completion)
            db.commit()
    else:
        if existing_completion:
            db.delete(existing_completion)
            db.commit()

    # Calculate latest progress
    all_modules = course.modules
    required_modules = [m for m in all_modules if m.is_required]
    total_required = len(required_modules)

    completed_records = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id == course_id
    ).all()
    completed_module_ids = {c.module_id for c in completed_records}

    completed_required_count = sum(1 for m in required_modules if m.id in completed_module_ids)
    remaining_titles = [m.title for m in required_modules if m.id not in completed_module_ids]

    progress_percentage = 0.0
    if total_required > 0:
        progress_percentage = round((completed_required_count / total_required) * 100, 1)

    quiz_unlocked = (total_required > 0 and completed_required_count >= total_required)

    return CourseProgressOut(
        course_id=course.id,
        course_title=course.title,
        total_required_modules=total_required,
        completed_required_modules=completed_required_count,
        progress_percentage=min(progress_percentage, 100.0),
        quiz_unlocked=quiz_unlocked,
        remaining_required_modules=remaining_titles
    )


@router.get("/{course_id}/progress", response_model=CourseProgressOut)
def get_course_progress(
    course_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    course = db.query(Course).filter(Course.id == course_id).first()
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    all_modules = course.modules
    required_modules = [m for m in all_modules if m.is_required]
    total_required = len(required_modules)

    completed_records = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id == course_id
    ).all()
    completed_module_ids = {c.module_id for c in completed_records}

    completed_required_count = sum(1 for m in required_modules if m.id in completed_module_ids)
    remaining_titles = [m.title for m in required_modules if m.id not in completed_module_ids]

    progress_percentage = 0.0
    if total_required > 0:
        progress_percentage = round((completed_required_count / total_required) * 100, 1)

    quiz_unlocked = (total_required > 0 and completed_required_count >= total_required)

    return CourseProgressOut(
        course_id=course.id,
        course_title=course.title,
        total_required_modules=total_required,
        completed_required_modules=completed_required_count,
        progress_percentage=min(progress_percentage, 100.0),
        quiz_unlocked=quiz_unlocked,
        remaining_required_modules=remaining_titles
    )
