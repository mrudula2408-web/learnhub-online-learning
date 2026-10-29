from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.models import Course, Enrollment, ModuleCompletion, User
from app.schemas.schemas import EnrollmentCreate, EnrollmentOut, CourseListOut
from app.api.deps import get_current_user

router = APIRouter(prefix="/enrollments", tags=["Enrollments"])

@router.post("", response_model=EnrollmentOut, status_code=status.HTTP_201_CREATED)
def enroll_course(
    enroll_in: EnrollmentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    course = db.query(Course).filter(Course.id == enroll_in.course_id).first()
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    existing = db.query(Enrollment).filter(
        Enrollment.user_id == current_user.id,
        Enrollment.course_id == course.id
    ).first()

    if existing:
        # Return existing enrollment idempotently
        modules = course.modules
        total_modules = len(modules)
        required_modules = [m for m in modules if m.is_required]
        comp_count = db.query(ModuleCompletion).filter(
            ModuleCompletion.user_id == current_user.id,
            ModuleCompletion.course_id == course.id
        ).count()
        progress_pct = round((comp_count / len(required_modules)) * 100, 1) if required_modules else 0.0

        course_out = CourseListOut(
            id=course.id,
            title=course.title,
            slug=course.slug,
            short_description=course.short_description,
            instructor_name=course.instructor_name,
            instructor_role=course.instructor_role,
            category=course.category,
            difficulty=course.difficulty,
            duration_hours=course.duration_hours,
            thumbnail_url=course.thumbnail_url,
            total_modules=total_modules,
            is_enrolled=True,
            progress_percentage=min(progress_pct, 100.0)
        )
        return EnrollmentOut(
            id=existing.id,
            course_id=existing.course_id,
            status=existing.status,
            enrolled_at=existing.enrolled_at,
            course=course_out
        )

    new_enrollment = Enrollment(
        user_id=current_user.id,
        course_id=course.id,
        status="active"
    )
    db.add(new_enrollment)
    db.commit()
    db.refresh(new_enrollment)

    course_out = CourseListOut(
        id=course.id,
        title=course.title,
        slug=course.slug,
        short_description=course.short_description,
        instructor_name=course.instructor_name,
        instructor_role=course.instructor_role,
        category=course.category,
        difficulty=course.difficulty,
        duration_hours=course.duration_hours,
        thumbnail_url=course.thumbnail_url,
        total_modules=len(course.modules),
        is_enrolled=True,
        progress_percentage=0.0
    )

    return EnrollmentOut(
        id=new_enrollment.id,
        course_id=new_enrollment.course_id,
        status=new_enrollment.status,
        enrolled_at=new_enrollment.enrolled_at,
        course=course_out
    )


@router.get("", response_model=List[EnrollmentOut])
def list_user_enrollments(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    enrollments = db.query(Enrollment).filter(
        Enrollment.user_id == current_user.id
    ).order_by(Enrollment.enrolled_at.desc()).all()

    results = []
    for en in enrollments:
        course = en.course
        modules = course.modules
        total_modules = len(modules)
        required_modules = [m for m in modules if m.is_required]
        comp_count = db.query(ModuleCompletion).filter(
            ModuleCompletion.user_id == current_user.id,
            ModuleCompletion.course_id == course.id
        ).count()
        progress_pct = round((comp_count / len(required_modules)) * 100, 1) if required_modules else 0.0

        course_out = CourseListOut(
            id=course.id,
            title=course.title,
            slug=course.slug,
            short_description=course.short_description,
            instructor_name=course.instructor_name,
            instructor_role=course.instructor_role,
            category=course.category,
            difficulty=course.difficulty,
            duration_hours=course.duration_hours,
            thumbnail_url=course.thumbnail_url,
            total_modules=total_modules,
            is_enrolled=True,
            progress_percentage=min(progress_pct, 100.0)
        )

        results.append(
            EnrollmentOut(
                id=en.id,
                course_id=en.course_id,
                status=en.status,
                enrolled_at=en.enrolled_at,
                course=course_out
            )
        )
    return results
