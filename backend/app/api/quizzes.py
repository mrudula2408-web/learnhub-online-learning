from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.models import (
    Course, Module, ModuleCompletion, Quiz, QuizQuestion, QuizOption, QuizAttempt, Enrollment, User
)
from app.schemas.schemas import (
    QuizDetailOut, QuizSubmission, QuizResultOut, QuizQuestionOut, QuizOptionOut, QuizQuestionReview
)
from app.api.deps import get_current_user

router = APIRouter(prefix="/courses", tags=["Quizzes"])

@router.get("/{course_id}/quiz", response_model=QuizDetailOut)
def get_course_quiz(
    course_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    course = db.query(Course).filter(Course.id == course_id).first()
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    quiz = db.query(Quiz).filter(Quiz.course_id == course_id).first()
    if not quiz:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Quiz not found for this course")

    # Check required modules completion
    required_modules = [m for m in course.modules if m.is_required]
    completed_records = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id == course_id
    ).all()
    completed_module_ids = {c.module_id for c in completed_records}

    missing_modules = [m.title for m in required_modules if m.id not in completed_module_ids]
    quiz_unlocked = len(missing_modules) == 0 and len(required_modules) > 0

    if not quiz_unlocked:
        # Quiz is locked: return locked state without questions or options to prevent cheating
        return QuizDetailOut(
            id=quiz.id,
            course_id=course.id,
            course_title=course.title,
            title=quiz.title,
            description=quiz.description,
            passing_score=quiz.passing_score,
            time_limit_minutes=quiz.time_limit_minutes,
            total_questions=len(quiz.questions),
            quiz_unlocked=False,
            missing_modules=missing_modules,
            questions=None
        )

    # Quiz is unlocked: return questions and options WITHOUT revealing is_correct
    questions_out = []
    for q in quiz.questions:
        options_out = [
            QuizOptionOut(id=opt.id, option_text=opt.option_text, order_index=opt.order_index)
            for opt in q.options
        ]
        questions_out.append(
            QuizQuestionOut(
                id=q.id,
                question_text=q.question_text,
                order_index=q.order_index,
                options=options_out
            )
        )

    return QuizDetailOut(
        id=quiz.id,
        course_id=course.id,
        course_title=course.title,
        title=quiz.title,
        description=quiz.description,
        passing_score=quiz.passing_score,
        time_limit_minutes=quiz.time_limit_minutes,
        total_questions=len(questions_out),
        quiz_unlocked=True,
        missing_modules=[],
        questions=questions_out
    )


@router.post("/{course_id}/quiz/submit", response_model=QuizResultOut)
def submit_course_quiz(
    course_id: int,
    submission: QuizSubmission,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    course = db.query(Course).filter(Course.id == course_id).first()
    if not course:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Course not found")

    quiz = db.query(Quiz).filter(Quiz.course_id == course_id).first()
    if not quiz:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Quiz not found")

    # Enforce backend unlocking rule
    required_modules = [m for m in course.modules if m.is_required]
    completed_records = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id == course_id
    ).all()
    completed_module_ids = {c.module_id for c in completed_records}
    missing_modules = [m.title for m in required_modules if m.id not in completed_module_ids]

    if missing_modules:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Quiz is locked! You must first complete all required modules: {', '.join(missing_modules)}"
        )

    # Process and grade submission
    user_answers_map = {ans.question_id: ans.selected_option_id for ans in submission.answers}
    
    score = 0
    total_questions = len(quiz.questions)
    review_list: List[QuizQuestionReview] = []

    for q in quiz.questions:
        selected_option_id = user_answers_map.get(q.id)
        correct_option = next((opt for opt in q.options if opt.is_correct), None)
        correct_opt_id = correct_option.id if correct_option else 0
        
        is_correct = (selected_option_id == correct_opt_id)
        if is_correct:
            score += 1

        options_data = [
            {"id": opt.id, "option_text": opt.option_text, "is_correct": opt.is_correct}
            for opt in q.options
        ]

        review_list.append(
            QuizQuestionReview(
                question_id=q.id,
                question_text=q.question_text,
                explanation=q.explanation,
                selected_option_id=selected_option_id,
                correct_option_id=correct_opt_id,
                is_correct=is_correct,
                options=options_data
            )
        )

    percentage = round((score / total_questions * 100), 1) if total_questions > 0 else 0.0
    passed = percentage >= quiz.passing_score

    # Save attempt in database
    attempt = QuizAttempt(
        user_id=current_user.id,
        quiz_id=quiz.id,
        course_id=course.id,
        score=score,
        total_questions=total_questions,
        percentage=percentage,
        passed=passed,
        answers_summary=user_answers_map
    )
    db.add(attempt)

    # Update enrollment status if passed
    if passed:
        enrollment = db.query(Enrollment).filter(
            Enrollment.user_id == current_user.id,
            Enrollment.course_id == course.id
        ).first()
        if enrollment:
            enrollment.status = "completed"

    db.commit()
    db.refresh(attempt)

    return QuizResultOut(
        id=attempt.id,
        quiz_id=quiz.id,
        course_id=course.id,
        score=score,
        total_questions=total_questions,
        percentage=percentage,
        passed=passed,
        passing_score=quiz.passing_score,
        review=review_list,
        submitted_at=attempt.submitted_at
    )
