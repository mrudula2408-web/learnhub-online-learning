from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.models import Course, Module, Enrollment, ModuleCompletion, Quiz, QuizAttempt, User
from app.schemas.schemas import DashboardStatsOut, DashboardCourseProgress
from app.api.deps import get_current_user

router = APIRouter(prefix="/dashboard", tags=["Dashboard"])

@router.get("/stats", response_model=DashboardStatsOut)
def get_dashboard_stats(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    enrollments = db.query(Enrollment).filter(Enrollment.user_id == current_user.id).all()
    enrolled_course_ids = [e.course_id for e in enrollments]

    courses = db.query(Course).filter(Course.id.in_(enrolled_course_ids)).all() if enrolled_course_ids else []

    all_user_completions = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id
    ).all()
    completed_module_ids = {c.module_id for c in all_user_completions}

    # Recent quiz attempts
    attempts = db.query(QuizAttempt).filter(
        QuizAttempt.user_id == current_user.id
    ).order_by(QuizAttempt.submitted_at.desc()).limit(10).all()

    attempts_by_course = {}
    for att in attempts:
        if att.course_id not in attempts_by_course:
            attempts_by_course[att.course_id] = att

    course_progress_list = []
    total_required_modules_across_all = 0
    completed_required_modules_across_all = 0

    for c in courses:
        modules = c.modules
        total_modules = len(modules)
        required_modules = [m for m in modules if m.is_required]
        total_required = len(required_modules)
        
        comp_count = sum(1 for m in required_modules if m.id in completed_module_ids)
        total_required_modules_across_all += total_required
        completed_required_modules_across_all += comp_count

        prog_pct = round((comp_count / total_required) * 100, 1) if total_required > 0 else 0.0
        quiz_unlocked = (total_required > 0 and comp_count >= total_required)

        latest_att = attempts_by_course.get(c.id)
        quiz_passed = any(att.course_id == c.id and att.passed for att in attempts)
        latest_score = latest_att.percentage if latest_att else None

        course_progress_list.append(
            DashboardCourseProgress(
                course_id=c.id,
                course_title=c.title,
                course_slug=c.slug,
                thumbnail_url=c.thumbnail_url,
                category=c.category,
                difficulty=c.difficulty,
                total_modules=total_modules,
                completed_modules=comp_count,
                progress_percentage=min(prog_pct, 100.0),
                quiz_unlocked=quiz_unlocked,
                quiz_passed=quiz_passed,
                latest_quiz_score=latest_score
            )
        )

    overall_pct = (
        round((completed_required_modules_across_all / total_required_modules_across_all) * 100, 1)
        if total_required_modules_across_all > 0 else 0.0
    )

    recent_attempts_data = [
        {
            "id": att.id,
            "quiz_id": att.quiz_id,
            "course_id": att.course_id,
            "course_title": att.quiz.course.title if att.quiz and att.quiz.course else "Course",
            "score": att.score,
            "total_questions": att.total_questions,
            "percentage": att.percentage,
            "passed": att.passed,
            "submitted_at": att.submitted_at.isoformat()
        }
        for att in attempts
    ]

    return DashboardStatsOut(
        total_enrolled_courses=len(enrollments),
        completed_modules_count=completed_required_modules_across_all,
        total_modules_count=total_required_modules_across_all,
        overall_progress_percentage=min(overall_pct, 100.0),
        courses=course_progress_list,
        recent_attempts=recent_attempts_data
    )
