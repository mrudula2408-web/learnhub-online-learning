from typing import List, Dict, Any, Union
from fastapi import APIRouter, Depends, HTTPException, status, Body
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.models import (
    Course, Module, ModuleCompletion, Quiz, QuizQuestion, QuizOption, QuizAttempt, Enrollment, User
)
from app.api.deps import get_current_user

router = APIRouter(tags=["Frontend Compatibility API"])

@router.get("/courses/{course_id}/modules")
def get_course_modules_compat(
    course_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    course = db.query(Course).filter(Course.id == course_id).first()
    if not course:
        raise HTTPException(status_code=404, detail="Course not found")

    # Ensure enrollment
    enrollment = db.query(Enrollment).filter(
        Enrollment.user_id == current_user.id,
        Enrollment.course_id == course_id
    ).first()
    if not enrollment:
        enrollment = Enrollment(user_id=current_user.id, course_id=course_id, status="active")
        db.add(enrollment)
        db.commit()

    user_completions = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id == course_id
    ).all()
    completed_ids = {c.module_id for c in user_completions}

    all_modules = course.modules
    required_modules = [m for m in all_modules if m.is_required]
    completed_count = sum(1 for m in all_modules if m.id in completed_ids)
    completed_required_count = sum(1 for m in required_modules if m.id in completed_ids)

    total_required = len(required_modules)
    progress_percentage = round((completed_required_count / total_required) * 100, 1) if total_required > 0 else 0.0
    quiz_unlocked = (total_required > 0 and completed_required_count >= total_required)

    mapped_modules = []
    for m in all_modules:
        mapped_modules.append({
            "moduleId": m.id,
            "id": m.id,
            "courseId": m.course_id,
            "title": m.title,
            "description": m.description,
            "learningObjectives": m.learning_objectives or [],
            "resourceTitle": m.resource_title,
            "resourceUrl": m.resource_url,
            "resourceType": m.resource_type,
            "orderIndex": m.order_index,
            "isRequired": m.is_required,
            "estimatedMinutes": m.estimated_minutes,
            "completed": m.id in completed_ids,
            "isCompleted": m.id in completed_ids,
        })

    return {
        "courseId": course.id,
        "courseTitle": course.title,
        "instructor": course.instructor_name,
        "totalModules": len(all_modules),
        "completedCount": completed_count,
        "progressPercentage": min(progress_percentage, 100.0),
        "quizUnlocked": quiz_unlocked,
        "modules": mapped_modules
    }


@router.post("/modules/{module_id}/complete")
def complete_module_compat(
    module_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    module = db.query(Module).filter(Module.id == module_id).first()
    if not module:
        raise HTTPException(status_code=404, detail="Module not found")

    course_id = module.course_id

    # Ensure enrollment
    enrollment = db.query(Enrollment).filter(
        Enrollment.user_id == current_user.id,
        Enrollment.course_id == course_id
    ).first()
    if not enrollment:
        enrollment = Enrollment(user_id=current_user.id, course_id=course_id, status="active")
        db.add(enrollment)
        db.flush()

    existing = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.module_id == module_id
    ).first()

    if not existing:
        completion = ModuleCompletion(
            user_id=current_user.id,
            module_id=module_id,
            course_id=course_id
        )
        db.add(completion)
        db.commit()

    return {
        "success": True,
        "message": f"Module '{module.title}' marked as completed!",
        "moduleId": module_id
    }


@router.get("/quizzes/{quiz_or_course_id}/status")
def get_quiz_status_compat(
    quiz_or_course_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    # Try by quiz_id or course_id
    quiz = db.query(Quiz).filter(
        (Quiz.id == quiz_or_course_id) | (Quiz.course_id == quiz_or_course_id)
    ).first()
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz not found")

    course = quiz.course
    required_modules = [m for m in course.modules if m.is_required]
    completed_records = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id == course.id
    ).all()
    completed_ids = {c.module_id for c in completed_records}

    missing_modules = [m.title for m in required_modules if m.id not in completed_ids]
    unlocked = len(missing_modules) == 0 and len(required_modules) > 0

    if not unlocked:
        return {
            "locked": True,
            "unlocked": False,
            "quizId": quiz.id,
            "courseId": course.id,
            "missingModules": missing_modules,
            "message": f"Quiz is locked! You must first complete all required modules: {', '.join(missing_modules)}"
        }

    return {
        "locked": False,
        "unlocked": True,
        "quizId": quiz.id,
        "courseId": course.id,
        "missingModules": [],
        "message": "Quiz is unlocked! You are ready to take the assessment."
    }


@router.get("/quizzes/course/{course_id}")
def get_quizzes_by_course_compat(
    course_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    quizzes = db.query(Quiz).filter(Quiz.course_id == course_id).all()
    results = []
    for q in quizzes:
        results.append({
            "quizId": q.id,
            "id": q.id,
            "courseId": q.course_id,
            "title": q.title,
            "description": q.description,
            "passingScore": q.passing_score,
            "timeLimitMinutes": q.time_limit_minutes,
            "totalQuestions": len(q.questions)
        })
    return results


@router.get("/quizzes/{quiz_id}/questions")
def get_quiz_questions_compat(
    quiz_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz not found")

    # Enforce unlock rule
    course = quiz.course
    required_modules = [m for m in course.modules if m.is_required]
    completed_records = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id == course.id
    ).all()
    completed_ids = {c.module_id for c in completed_records}
    missing_modules = [m.title for m in required_modules if m.id not in completed_ids]

    if missing_modules:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Quiz is locked! You must first complete all required modules: {', '.join(missing_modules)}"
        )

    results = []
    option_letters = ["A", "B", "C", "D", "E"]
    for q in quiz.questions:
        options = q.options
        opt_map = {}
        for idx, opt in enumerate(options):
            letter = option_letters[idx] if idx < len(option_letters) else f"Opt{idx+1}"
            opt_map[f"option{letter}"] = opt.option_text

        results.append({
            "questionId": q.id,
            "id": q.id,
            "quizId": q.quiz_id,
            "questionText": q.question_text,
            "orderIndex": q.order_index,
            "options": [{"id": opt.id, "text": opt.option_text, "orderIndex": opt.order_index} for opt in options],
            **opt_map
        })
    return results


@router.post("/quizzes/{quiz_id}/submit")
def submit_quiz_compat(
    quiz_id: int,
    payload: Dict[str, Any] = Body(...),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz not found")

    # Enforce unlock rule
    course = quiz.course
    required_modules = [m for m in course.modules if m.is_required]
    completed_records = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id == course.id
    ).all()
    completed_ids = {c.module_id for c in completed_records}
    missing_modules = [m.title for m in required_modules if m.id not in completed_ids]

    if missing_modules:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Quiz is locked! You must first complete all required modules: {', '.join(missing_modules)}"
        )

    # Extract answers from payload (can be { answers: { "1": "B" } } or { "1": "B" })
    raw_answers = payload.get("answers", payload)
    if isinstance(raw_answers, list):
        # [{ question_id: 1, selected_option_id: 2 }]
        answer_dict = {str(item.get("question_id") or item.get("questionId")): item.get("selected_option_id") or item.get("selectedOptionId") for item in raw_answers}
    else:
        answer_dict = raw_answers

    option_letters = ["A", "B", "C", "D", "E"]
    score = 0
    total_questions = len(quiz.questions)
    summary = {}
    review = []

    for q in quiz.questions:
        q_id_str = str(q.id)
        chosen = answer_dict.get(q_id_str)
        summary[q_id_str] = chosen

        correct_opt = next((o for o in q.options if o.is_correct), None)
        correct_opt_id = correct_opt.id if correct_opt else None

        # Determine letter of correct option
        correct_letter = None
        for idx, opt in enumerate(q.options):
            if opt.is_correct and idx < len(option_letters):
                correct_letter = option_letters[idx]

        is_match = False
        if chosen is not None:
            # Check if chosen matches option_id
            if str(chosen).isdigit() and int(chosen) == correct_opt_id:
                is_match = True
            elif str(chosen).upper() == correct_letter:
                is_match = True

        if is_match:
            score += 1

        review.append({
            "questionId": q.id,
            "questionText": q.question_text,
            "explanation": q.explanation,
            "chosen": chosen,
            "correct": correct_letter or correct_opt_id,
            "isCorrect": is_match
        })

    percentage = round((score / total_questions) * 100, 1) if total_questions > 0 else 0.0
    passed = percentage >= quiz.passing_score

    attempt = QuizAttempt(
        user_id=current_user.id,
        quiz_id=quiz.id,
        course_id=course.id,
        score=score,
        total_questions=total_questions,
        percentage=percentage,
        passed=passed,
        answers_summary=summary
    )
    db.add(attempt)

    if passed:
        enrollment = db.query(Enrollment).filter(
            Enrollment.user_id == current_user.id,
            Enrollment.course_id == course.id
        ).first()
        if enrollment:
            enrollment.status = "completed"

    db.commit()

    return {
        "score": score,
        "totalQuestions": total_questions,
        "percentage": percentage,
        "passed": passed,
        "passingScore": quiz.passing_score,
        "review": review,
        "message": f"Quiz submitted! You scored {score}/{total_questions} ({percentage}%)."
    }


@router.post("/enrollments")
def enroll_course_compat(
    payload: Dict[str, Any] = Body(...),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    course_id = payload.get("courseId") or payload.get("course_id") or payload.get("id")
    if not course_id:
        raise HTTPException(status_code=400, detail="courseId or course_id is required")

    course = db.query(Course).filter(Course.id == int(course_id)).first()
    if not course:
        raise HTTPException(status_code=404, detail="Course not found")

    existing = db.query(Enrollment).filter(
        Enrollment.user_id == current_user.id,
        Enrollment.course_id == course.id
    ).first()

    if not existing:
        existing = Enrollment(user_id=current_user.id, course_id=course.id, status="active")
        db.add(existing)
        db.commit()
        db.refresh(existing)

    return {
        "enrollmentId": existing.id,
        "id": existing.id,
        "courseId": course.id,
        "status": existing.status,
        "message": f"Successfully enrolled in {course.title}"
    }


@router.get("/enrollments/my")
def get_my_enrollments_compat(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    enrollments = db.query(Enrollment).filter(Enrollment.user_id == current_user.id).all()
    results = []
    for en in enrollments:
        c = en.course
        results.append({
            "enrollmentId": en.id,
            "id": en.id,
            "courseId": c.id,
            "courseTitle": c.title,
            "title": c.title,
            "shortDescription": c.short_description,
            "instructor": c.instructor_name,
            "category": c.category,
            "difficulty": c.difficulty,
            "durationHours": c.duration_hours,
            "thumbnailUrl": c.thumbnail_url,
            "enrolledAt": en.enrolled_at.isoformat() if en.enrolled_at else None,
            "status": en.status
        })
    return results


@router.get("/progress/my")
def get_my_progress_compat(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    enrollments = db.query(Enrollment).filter(Enrollment.user_id == current_user.id).all()
    results = []
    for en in enrollments:
        c = en.course
        modules = c.modules
        total_modules = len(modules)
        required_modules = [m for m in modules if m.is_required]
        comp_count = db.query(ModuleCompletion).filter(
            ModuleCompletion.user_id == current_user.id,
            ModuleCompletion.course_id == c.id
        ).count()
        total_req = len(required_modules)
        pct = round((comp_count / total_req) * 100, 1) if total_req > 0 else 0.0

        quiz_unlocked = (total_req > 0 and comp_count >= total_req)
        status_str = "COMPLETED" if pct >= 100.0 else "IN_PROGRESS"

        results.append({
            "courseId": c.id,
            "courseTitle": c.title,
            "category": c.category,
            "completedModules": comp_count,
            "totalModules": total_modules,
            "completionPercentage": min(pct, 100.0),
            "completionStatus": status_str,
            "quizUnlocked": quiz_unlocked
        })
    return results


@router.get("/quizzes/attempts/my")
def get_my_quiz_attempts_compat(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    attempts = db.query(QuizAttempt).filter(
        QuizAttempt.user_id == current_user.id
    ).order_by(QuizAttempt.submitted_at.desc()).all()

    results = []
    for att in attempts:
        quiz = att.quiz
        course = quiz.course if quiz else None
        results.append({
            "attemptId": att.id,
            "id": att.id,
            "quizId": att.quiz_id,
            "quizTitle": quiz.title if quiz else "Quiz",
            "courseId": att.course_id,
            "courseTitle": course.title if course else "Course",
            "score": att.score,
            "totalQuestions": att.total_questions,
            "percentage": att.percentage,
            "passed": att.passed,
            "attemptedAt": att.submitted_at.isoformat() if att.submitted_at else None
        })
    return results


@router.get("/modules/summary")
def get_modules_summary_compat(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    enrollments = db.query(Enrollment).filter(Enrollment.user_id == current_user.id).all()
    enrolled_course_ids = [e.course_id for e in enrollments]

    total_modules = db.query(Module).filter(Module.course_id.in_(enrolled_course_ids)).count() if enrolled_course_ids else 0
    completed_modules = db.query(ModuleCompletion).filter(
        ModuleCompletion.user_id == current_user.id,
        ModuleCompletion.course_id.in_(enrolled_course_ids)
    ).count() if enrolled_course_ids else 0

    overall_pct = round((completed_modules / total_modules) * 100, 1) if total_modules > 0 else 0.0

    return {
        "totalModules": total_modules,
        "completedModules": completed_modules,
        "overallPercentage": min(overall_pct, 100.0),
        "enrolledCoursesCount": len(enrolled_course_ids)
    }
