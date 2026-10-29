from pydantic import BaseModel, EmailStr, Field
from typing import List, Optional, Any, Dict
from datetime import datetime

# --- AUTH SCHEMAS ---

class UserRegister(BaseModel):
    email: EmailStr
    password: str = Field(..., min_length=6, description="Password must be at least 6 characters")
    full_name: str = Field(..., min_length=2, max_length=100)

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class UserOut(BaseModel):
    id: int
    email: EmailStr
    full_name: str
    avatar_url: Optional[str] = None
    role: str
    created_at: datetime

    class Config:
        from_attributes = True

class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user: UserOut


# --- MODULE SCHEMAS ---

class ModuleOut(BaseModel):
    id: int
    course_id: int
    title: str
    description: str
    learning_objectives: List[str] = []
    resource_title: str
    resource_url: str
    resource_type: str
    order_index: int
    is_required: bool
    estimated_minutes: int
    is_completed: bool = False

    class Config:
        from_attributes = True


# --- COURSE SCHEMAS ---

class CourseListOut(BaseModel):
    id: int
    title: str
    slug: str
    short_description: str
    instructor_name: str
    instructor_role: str
    category: str
    difficulty: str
    duration_hours: float
    thumbnail_url: Optional[str] = None
    total_modules: int = 0
    is_enrolled: bool = False
    progress_percentage: float = 0.0

    class Config:
        from_attributes = True

class CourseDetailOut(BaseModel):
    id: int
    title: str
    slug: str
    description: str
    short_description: str
    instructor_name: str
    instructor_role: str
    category: str
    difficulty: str
    duration_hours: float
    thumbnail_url: Optional[str] = None
    is_enrolled: bool = False
    modules: List[ModuleOut] = []
    completed_modules_count: int = 0
    total_required_modules: int = 0
    completed_required_modules: int = 0
    progress_percentage: float = 0.0
    quiz_unlocked: bool = False
    quiz_passed: bool = False
    quiz_id: Optional[int] = None

    class Config:
        from_attributes = True


# --- ENROLLMENT SCHEMAS ---

class EnrollmentCreate(BaseModel):
    course_id: Optional[int] = None
    courseId: Optional[int] = None

    @property
    def target_course_id(self) -> Optional[int]:
        return self.course_id or self.courseId

class EnrollmentOut(BaseModel):
    id: int
    course_id: int
    status: str
    enrolled_at: datetime
    course: CourseListOut

    class Config:
        from_attributes = True


# --- PROGRESS SCHEMAS ---

class ModuleCompletionToggle(BaseModel):
    completed: bool

class CourseProgressOut(BaseModel):
    course_id: int
    course_title: str
    total_required_modules: int
    completed_required_modules: int
    progress_percentage: float
    quiz_unlocked: bool
    remaining_required_modules: List[str] = []


# --- QUIZ SCHEMAS ---

class QuizOptionOut(BaseModel):
    id: int
    option_text: str
    order_index: int

    class Config:
        from_attributes = True

class QuizQuestionOut(BaseModel):
    id: int
    question_text: str
    order_index: int
    options: List[QuizOptionOut]

    class Config:
        from_attributes = True

class QuizDetailOut(BaseModel):
    id: int
    course_id: int
    course_title: str
    title: str
    description: str
    passing_score: float
    time_limit_minutes: int
    total_questions: int
    quiz_unlocked: bool
    missing_modules: List[str] = []
    questions: Optional[List[QuizQuestionOut]] = None

class QuizAnswerSubmission(BaseModel):
    question_id: int
    selected_option_id: int

class QuizSubmission(BaseModel):
    answers: List[QuizAnswerSubmission]

class QuizQuestionReview(BaseModel):
    question_id: int
    question_text: str
    explanation: Optional[str] = None
    selected_option_id: Optional[int] = None
    correct_option_id: int
    is_correct: bool
    options: List[Dict[str, Any]]

class QuizResultOut(BaseModel):
    id: int
    quiz_id: int
    course_id: int
    score: int
    total_questions: int
    percentage: float
    passed: bool
    passing_score: float
    review: List[QuizQuestionReview]
    submitted_at: datetime

    class Config:
        from_attributes = True


# --- DASHBOARD SCHEMAS ---

class DashboardCourseProgress(BaseModel):
    course_id: int
    course_title: str
    course_slug: str
    thumbnail_url: Optional[str] = None
    category: str
    difficulty: str
    total_modules: int
    completed_modules: int
    progress_percentage: float
    quiz_unlocked: bool
    quiz_passed: bool
    latest_quiz_score: Optional[float] = None

class DashboardStatsOut(BaseModel):
    total_enrolled_courses: int
    completed_modules_count: int
    total_modules_count: int
    overall_progress_percentage: float
    courses: List[DashboardCourseProgress] = []
    recent_attempts: List[Dict[str, Any]] = []
