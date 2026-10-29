from datetime import datetime, timezone
from sqlalchemy import (
    Column, Integer, String, Text, Boolean, DateTime, ForeignKey, Float, JSON, UniqueConstraint
)
from sqlalchemy.orm import relationship
from app.core.database import Base

def utcnow():
    return datetime.now(timezone.utc)

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    full_name = Column(String(255), nullable=False)
    avatar_url = Column(String(500), nullable=True)
    role = Column(String(50), default="student", nullable=False)
    created_at = Column(DateTime(timezone=True), default=utcnow)
    updated_at = Column(DateTime(timezone=True), default=utcnow, onupdate=utcnow)

    enrollments = relationship("Enrollment", back_populates="user", cascade="all, delete-orphan")
    module_completions = relationship("ModuleCompletion", back_populates="user", cascade="all, delete-orphan")
    quiz_attempts = relationship("QuizAttempt", back_populates="user", cascade="all, delete-orphan")


class Course(Base):
    __tablename__ = "courses"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(255), nullable=False)
    slug = Column(String(255), unique=True, index=True, nullable=False)
    description = Column(Text, nullable=False)
    short_description = Column(String(500), nullable=False)
    instructor_name = Column(String(255), nullable=False)
    instructor_role = Column(String(255), nullable=False)
    category = Column(String(100), nullable=False)  # e.g., "Web Development", "AI & Data Science", "DevOps"
    difficulty = Column(String(50), nullable=False)  # "Beginner", "Intermediate", "Advanced"
    duration_hours = Column(Float, nullable=False, default=10.0)
    thumbnail_url = Column(String(500), nullable=True)
    is_published = Column(Boolean, default=True, nullable=False)
    created_at = Column(DateTime(timezone=True), default=utcnow)
    updated_at = Column(DateTime(timezone=True), default=utcnow, onupdate=utcnow)

    modules = relationship("Module", back_populates="course", cascade="all, delete-orphan", order_by="Module.order_index")
    quiz = relationship("Quiz", back_populates="course", uselist=False, cascade="all, delete-orphan")
    enrollments = relationship("Enrollment", back_populates="course", cascade="all, delete-orphan")


class Module(Base):
    __tablename__ = "modules"

    id = Column(Integer, primary_key=True, index=True)
    course_id = Column(Integer, ForeignKey("courses.id", ondelete="CASCADE"), nullable=False, index=True)
    title = Column(String(255), nullable=False)
    description = Column(Text, nullable=False)
    learning_objectives = Column(JSON, default=list)  # list of strings
    resource_title = Column(String(255), nullable=False)
    resource_url = Column(String(1000), nullable=False)
    resource_type = Column(String(50), default="documentation", nullable=False)  # documentation, textbook, tutorial, guide
    order_index = Column(Integer, default=1, nullable=False)
    is_required = Column(Boolean, default=True, nullable=False)
    estimated_minutes = Column(Integer, default=30, nullable=False)
    created_at = Column(DateTime(timezone=True), default=utcnow)

    course = relationship("Course", back_populates="modules")
    completions = relationship("ModuleCompletion", back_populates="module", cascade="all, delete-orphan")


class Enrollment(Base):
    __tablename__ = "enrollments"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    course_id = Column(Integer, ForeignKey("courses.id", ondelete="CASCADE"), nullable=False, index=True)
    status = Column(String(50), default="active", nullable=False)  # active, completed
    enrolled_at = Column(DateTime(timezone=True), default=utcnow)
    completed_at = Column(DateTime(timezone=True), nullable=True)

    __table_args__ = (UniqueConstraint("user_id", "course_id", name="uq_user_course_enrollment"),)

    user = relationship("User", back_populates="enrollments")
    course = relationship("Course", back_populates="enrollments")


class ModuleCompletion(Base):
    __tablename__ = "module_completions"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    module_id = Column(Integer, ForeignKey("modules.id", ondelete="CASCADE"), nullable=False, index=True)
    course_id = Column(Integer, ForeignKey("courses.id", ondelete="CASCADE"), nullable=False, index=True)
    completed_at = Column(DateTime(timezone=True), default=utcnow)

    __table_args__ = (UniqueConstraint("user_id", "module_id", name="uq_user_module_completion"),)

    user = relationship("User", back_populates="module_completions")
    module = relationship("Module", back_populates="completions")


class Quiz(Base):
    __tablename__ = "quizzes"

    id = Column(Integer, primary_key=True, index=True)
    course_id = Column(Integer, ForeignKey("courses.id", ondelete="CASCADE"), unique=True, nullable=False)
    title = Column(String(255), nullable=False)
    description = Column(Text, nullable=False)
    passing_score = Column(Float, default=70.0, nullable=False)  # percentage to pass
    time_limit_minutes = Column(Integer, default=15, nullable=False)
    created_at = Column(DateTime(timezone=True), default=utcnow)

    course = relationship("Course", back_populates="quiz")
    questions = relationship("QuizQuestion", back_populates="quiz", cascade="all, delete-orphan", order_by="QuizQuestion.order_index")
    attempts = relationship("QuizAttempt", back_populates="quiz", cascade="all, delete-orphan")


class QuizQuestion(Base):
    __tablename__ = "quiz_questions"

    id = Column(Integer, primary_key=True, index=True)
    quiz_id = Column(Integer, ForeignKey("quizzes.id", ondelete="CASCADE"), nullable=False, index=True)
    question_text = Column(Text, nullable=False)
    explanation = Column(Text, nullable=True)
    order_index = Column(Integer, default=1, nullable=False)

    quiz = relationship("Quiz", back_populates="questions")
    options = relationship("QuizOption", back_populates="question", cascade="all, delete-orphan", order_by="QuizOption.order_index")


class QuizOption(Base):
    __tablename__ = "quiz_options"

    id = Column(Integer, primary_key=True, index=True)
    question_id = Column(Integer, ForeignKey("quiz_questions.id", ondelete="CASCADE"), nullable=False, index=True)
    option_text = Column(String(500), nullable=False)
    is_correct = Column(Boolean, default=False, nullable=False)
    order_index = Column(Integer, default=1, nullable=False)

    question = relationship("QuizQuestion", back_populates="options")


class QuizAttempt(Base):
    __tablename__ = "quiz_attempts"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    quiz_id = Column(Integer, ForeignKey("quizzes.id", ondelete="CASCADE"), nullable=False, index=True)
    course_id = Column(Integer, ForeignKey("courses.id", ondelete="CASCADE"), nullable=False, index=True)
    score = Column(Integer, nullable=False)
    total_questions = Column(Integer, nullable=False)
    percentage = Column(Float, nullable=False)
    passed = Column(Boolean, default=False, nullable=False)
    answers_summary = Column(JSON, default=dict)  # question_id -> chosen_option_id
    submitted_at = Column(DateTime(timezone=True), default=utcnow)

    user = relationship("User", back_populates="quiz_attempts")
    quiz = relationship("Quiz", back_populates="attempts")
