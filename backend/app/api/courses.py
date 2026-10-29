from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.models import Course, Module, Enrollment, ModuleCompletion, Quiz, QuizAttempt, User
from app.schemas.schemas import CourseListOut, CourseDetailOut, ModuleOut
from app.api.deps import get_optional_current_user

router = APIRouter(prefix="/courses", tags=["Courses"])

@router.get("", response_model=List[CourseListOut])
def list_courses(
    category: Optional[str] = Query(None, description="Filter by category"),
    difficulty: Optional[str] = Query(None, description="Filter by difficulty"),
    search: Optional[str] = Query(None, description="Search course title or description"),
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    query = db.query(Course).filter(Course.is_published == True)
    
    if category and category.lower() != "all":
        query = query.filter(Course.category.ilike(f"%{category}%"))
    if difficulty and difficulty.lower() != "all":
        query = query.filter(Course.difficulty.ilike(f"%{difficulty}%"))
    if search:
        query = query.filter(
            (Course.title.ilike(f"%{search}%")) | (Course.description.ilike(f"%{search}%"))
        )
    
    courses = query.order_by(Course.id.asc()).all()
    
    # Pre-fetch user enrollments and completions if authenticated
    enrolled_course_ids = set()
    completions_map = {}  # course_id -> count of completed required modules
    
    if current_user:
        enrollments = db.query(Enrollment).filter(Enrollment.user_id == current_user.id).all()
        enrolled_course_ids = {e.course_id for e in enrollments}
        
        user_completions = db.query(ModuleCompletion).filter(
            ModuleCompletion.user_id == current_user.id
        ).all()
        for comp in user_completions:
            completions_map[comp.course_id] = completions_map.get(comp.course_id, 0) + 1

    result = []
    for c in courses:
        modules = c.modules
        total_modules = len(modules)
        required_modules = [m for m in modules if m.is_required]
        total_required = len(required_modules)
        
        is_enrolled = c.id in enrolled_course_ids
        progress_pct = 0.0
        if is_enrolled and total_required > 0:
            completed_cnt = completions_map.get(c.id, 0)
            progress_pct = round((completed_cnt / total_required) * 100, 1)
            if progress_pct > 100.0:
                progress_pct = 100.0

        result.append(
            CourseListOut(
                id=c.id,
                title=c.title,
                slug=c.slug,
                short_description=c.short_description,
                instructor_name=c.instructor_name,
                instructor_role=c.instructor_role,
                category=c.category,
                difficulty=c.difficulty,
                duration_hours=c.duration_hours,
                thumbnail_url=c.thumbnail_url,
                total_modules=total_modules,
                is_enrolled=is_enrolled,
                progress_percentage=progress_pct
            )
        )
    return result


@router.get("/{course_id_or_slug}", response_model=CourseDetailOut)
def get_course_detail(
    course_id_or_slug: str,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    if course_id_or_slug.isdigit():
        course = db.query(Course).filter(Course.id == int(course_id_or_slug)).first()
    else:
        course = db.query(Course).filter(Course.slug == course_id_or_slug).first()

    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    is_enrolled = False
    completed_module_ids = set()
    quiz_passed = False

    if current_user:
        enrollment = db.query(Enrollment).filter(
            Enrollment.user_id == current_user.id,
            Enrollment.course_id == course.id
        ).first()
        is_enrolled = enrollment is not None

        user_completions = db.query(ModuleCompletion).filter(
            ModuleCompletion.user_id == current_user.id,
            ModuleCompletion.course_id == course.id
        ).all()
        completed_module_ids = {c.module_id for c in user_completions}

        # Check if quiz was passed
        attempt = db.query(QuizAttempt).filter(
            QuizAttempt.user_id == current_user.id,
            QuizAttempt.course_id == course.id,
            QuizAttempt.passed == True
        ).first()
        quiz_passed = attempt is not None

    modules_out = []
    total_required = 0
    completed_required = 0

    for m in course.modules:
        if m.is_required:
            total_required += 1
        is_completed = m.id in completed_module_ids
        if m.is_required and is_completed:
            completed_required += 1

        modules_out.append(
            ModuleOut(
                id=m.id,
                course_id=m.course_id,
                title=m.title,
                description=m.description,
                learning_objectives=m.learning_objectives or [],
                resource_title=m.resource_title,
                resource_url=m.resource_url,
                resource_type=m.resource_type,
                order_index=m.order_index,
                is_required=m.is_required,
                estimated_minutes=m.estimated_minutes,
                is_completed=is_completed
            )
        )

    progress_percentage = 0.0
    if total_required > 0:
        progress_percentage = round((completed_required / total_required) * 100, 1)

    quiz_unlocked = (total_required > 0 and completed_required >= total_required)

    return CourseDetailOut(
        id=course.id,
        title=course.title,
        slug=course.slug,
        description=course.description,
        short_description=course.short_description,
        instructor_name=course.instructor_name,
        instructor_role=course.instructor_role,
        category=course.category,
        difficulty=course.difficulty,
        duration_hours=course.duration_hours,
        thumbnail_url=course.thumbnail_url,
        is_enrolled=is_enrolled,
        modules=modules_out,
        completed_modules_count=len(completed_module_ids),
        total_required_modules=total_required,
        completed_required_modules=completed_required,
        progress_percentage=progress_percentage,
        quiz_unlocked=quiz_unlocked,
        quiz_passed=quiz_passed,
        quiz_id=course.quiz.id if course.quiz else None
    )
