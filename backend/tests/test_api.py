import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os

from app.core.database import Base, get_db
from app.main import app
from app.services.seed import seed_database

# Use in-memory SQLite for high-speed isolated tests
TEST_DATABASE_URL = "sqlite:///./test_nextlearn.db"

test_engine = create_engine(TEST_DATABASE_URL, connect_args={"check_same_thread": False})
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=test_engine)

def override_get_db():
    db = TestingSessionLocal()
    try:
        yield db
    finally:
        db.close()

app.dependency_overrides[get_db] = override_get_db

@pytest.fixture(scope="module", autouse=True)
def setup_db():
    Base.metadata.create_all(bind=test_engine)
    db = TestingSessionLocal()
    seed_database(db)
    db.close()
    yield
    Base.metadata.drop_all(bind=test_engine)
    if os.path.exists("./test_nextlearn.db"):
        try:
            os.remove("./test_nextlearn.db")
        except Exception:
            pass

client = TestClient(app)

def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "ok"
    assert data["app"] == "NextLearn API"
    assert data["database"] == "connected"

def test_courses_listing():
    response = client.get("/api/courses")
    assert response.status_code == 200
    courses = response.json()
    assert len(courses) >= 3
    assert any("React & FastAPI" in c["title"] for c in courses)

def test_user_registration_and_login():
    # 1. Register new student
    reg_payload = {
        "email": "tester@nextlearn.edu",
        "password": "Password123!",
        "full_name": "Test Student"
    }
    reg_res = client.post("/api/auth/register", json=reg_payload)
    assert reg_res.status_code == 201
    reg_data = reg_res.json()
    assert "access_token" in reg_data
    assert reg_data["user"]["email"] == "tester@nextlearn.edu"

    # 2. Duplicate registration should fail
    dup_res = client.post("/api/auth/register", json=reg_payload)
    assert dup_res.status_code == 400

    # 3. Login
    login_payload = {
        "email": "tester@nextlearn.edu",
        "password": "Password123!"
    }
    login_res = client.post("/api/auth/login", json=login_payload)
    assert login_res.status_code == 200
    token = login_res.json()["access_token"]


    # 4. Auth me
    me_res = client.get("/api/auth/me", headers={"Authorization": f"Bearer {token}"})
    assert me_res.status_code == 200
    assert me_res.json()["full_name"] == "Test Student"

def test_course_flow_and_quiz_unlocking():
    # Login as student
    login_res = client.post("/api/auth/login", json={"email": "tester@nextlearn.edu", "password": "Password123!"})
    token = login_res.json()["access_token"]

    headers = {"Authorization": f"Bearer {token}"}

    # 1. Get course details
    c_res = client.get("/api/courses/full-stack-react-fastapi", headers=headers)
    assert c_res.status_code == 200
    course = c_res.json()
    course_id = course["id"]
    modules = course["modules"]
    assert len(modules) == 4
    assert course["quiz_unlocked"] is False

    # 2. Check quiz is locked before completing modules
    quiz_res = client.get(f"/api/courses/{course_id}/quiz", headers=headers)
    assert quiz_res.status_code == 200
    quiz_data = quiz_res.json()
    assert quiz_data["quiz_unlocked"] is False
    assert len(quiz_data["missing_modules"]) == 4
    assert quiz_data["questions"] is None  # Questions hidden while locked!

    # 3. Attempting to submit quiz while locked must be forbidden (403)
    hack_submit = client.post(
        f"/api/courses/{course_id}/quiz/submit",
        json={"answers": []},
        headers=headers
    )
    assert hack_submit.status_code == 403

    # 4. Enroll in course
    enroll_res = client.post("/api/enrollments", json={"course_id": course_id}, headers=headers)
    assert enroll_res.status_code in (200, 201)

    # 5. Mark first 3 modules complete
    for m in modules[:3]:
        comp_res = client.post(
            f"/api/courses/{course_id}/modules/{m['id']}/complete",
            json={"completed": True},
            headers=headers
        )
        assert comp_res.status_code == 200

    # Still locked (1 module remaining)
    quiz_res2 = client.get(f"/api/courses/{course_id}/quiz", headers=headers)
    assert quiz_res2.json()["quiz_unlocked"] is False
    assert len(quiz_res2.json()["missing_modules"]) == 1

    # 6. Complete the last required module
    last_mod = modules[3]
    last_res = client.post(
        f"/api/courses/{course_id}/modules/{last_mod['id']}/complete",
        json={"completed": True},
        headers=headers
    )
    assert last_res.status_code == 200
    assert last_res.json()["progress_percentage"] == 100.0
    assert last_res.json()["quiz_unlocked"] is True

    # 7. Now quiz must be unlocked!
    unlocked_quiz_res = client.get(f"/api/courses/{course_id}/quiz", headers=headers)
    assert unlocked_quiz_res.status_code == 200
    unlocked_quiz = unlocked_quiz_res.json()
    assert unlocked_quiz["quiz_unlocked"] is True
    assert unlocked_quiz["questions"] is not None
    assert len(unlocked_quiz["questions"]) == 4

    # 8. Submit quiz answers
    questions = unlocked_quiz["questions"]
    answers = []
    for q in questions:
        # Pick the second option for testing
        answers.append({"question_id": q["id"], "selected_option_id": q["options"][1]["id"]})

    submit_res = client.post(
        f"/api/courses/{course_id}/quiz/submit",
        json={"answers": answers},
        headers=headers
    )
    assert submit_res.status_code == 200
    result = submit_res.json()
    assert "score" in result
    assert "percentage" in result
    assert len(result["review"]) == 4

    # 9. Verify dashboard stats
    dash_res = client.get("/api/dashboard/stats", headers=headers)
    assert dash_res.status_code == 200
    dash_data = dash_res.json()
    assert dash_data["total_enrolled_courses"] >= 1
    assert dash_data["completed_modules_count"] >= 4
    assert len(dash_data["recent_attempts"]) >= 1
