from sqlalchemy.orm import Session
from app.models.models import Course, Module, Quiz, QuizQuestion, QuizOption, User
from app.core.security import get_password_hash

SAMPLE_COURSES = [
    {
        "title": "Full-Stack Web Development with React & FastAPI",
        "slug": "full-stack-react-fastapi",
        "description": "Master end-to-end full stack web architecture. Learn how modern single-page applications built with React interact with high-performance Python FastAPI backends, secure JWT authentication, and cloud databases.",
        "short_description": "Build production-grade web applications with React, Vite, Tailwind CSS, Python FastAPI, and PostgreSQL.",
        "instructor_name": "Dr. Sarah Jenkins",
        "instructor_role": "Lead Software Architect & University Lecturer",
        "category": "Web Development",
        "difficulty": "Intermediate",
        "duration_hours": 14.5,
        "thumbnail_url": "https://images.unsplash.com/photo-1633356122544-f134324a6cee?q=80&w=1200&auto=format&fit=crop",
        "modules": [
            {
                "title": "Modern Frontend Fundamentals with React & Vite",
                "description": "Understand component hierarchy, state management with hooks (useState, useEffect), and responsive layout styling with Tailwind CSS.",
                "learning_objectives": [
                    "Set up blazing-fast React projects using Vite",
                    "Manage state and side-effects efficiently using React hooks",
                    "Build reusable, accessible UI component hierarchies"
                ],
                "resource_title": "Official React Documentation — Describing the UI",
                "resource_url": "https://react.dev/learn/describing-the-ui",
                "resource_type": "documentation",
                "order_index": 1,
                "is_required": True,
                "estimated_minutes": 45
            },
            {
                "title": "High-Performance REST APIs with FastAPI",
                "description": "Design asynchronous REST endpoints, utilize Pydantic schemas for data validation, and explore automatic OpenAPI documentation.",
                "learning_objectives": [
                    "Create asynchronous endpoints with Python FastAPI",
                    "Validate incoming requests and outgoing payloads with Pydantic v2",
                    "Inspect and test endpoints using interactive Swagger docs"
                ],
                "resource_title": "FastAPI Official Tutorial — First Steps",
                "resource_url": "https://fastapi.tiangolo.com/tutorial/first-steps/",
                "resource_type": "documentation",
                "order_index": 2,
                "is_required": True,
                "estimated_minutes": 40
            },
            {
                "title": "Cloud Database Architecture & SQLAlchemy ORM",
                "description": "Connect FastAPI to cloud PostgreSQL, define relational models, execute migrations, and implement robust query sessions.",
                "learning_objectives": [
                    "Design scalable database schemas with foreign keys and unique constraints",
                    "Prevent SQL injection using SQLAlchemy 2.0 ORM query building",
                    "Handle database connection pooling and failovers safely"
                ],
                "resource_title": "SQLAlchemy 2.0 Unified Tutorial",
                "resource_url": "https://docs.sqlalchemy.org/en/20/tutorial/index.html",
                "resource_type": "textbook",
                "order_index": 3,
                "is_required": True,
                "estimated_minutes": 50
            },
            {
                "title": "Production Deployment & Security Best Practices",
                "description": "Prepare production builds, configure CORS for cross-origin domain isolation, manage environment variables, and deploy to Vercel and cloud platforms.",
                "learning_objectives": [
                    "Enforce CORS origins and prevent unauthorized API access",
                    "Safely store credentials via environment variables without git leaks",
                    "Configure single-page application routing redirects on static hosts"
                ],
                "resource_title": "OWASP Web Security Testing Guide — Authentication",
                "resource_url": "https://owasp.org/www-project-web-security-testing-guide/latest/4-Web_Application_Security_Testing/04-Authentication_Testing/",
                "resource_type": "guide",
                "order_index": 4,
                "is_required": True,
                "estimated_minutes": 35
            }
        ],
        "quiz": {
            "title": "Full-Stack React & FastAPI Certification Quiz",
            "description": "Test your comprehensive understanding of React frontend state, FastAPI request lifecycles, and secure database integrations.",
            "passing_score": 75.0,
            "time_limit_minutes": 15,
            "questions": [
                {
                    "question_text": "Which React hook is used to perform side-effects such as fetching data from an external API after rendering?",
                    "explanation": "useEffect is specifically designed for side effects like API calls, event listeners, and DOM mutations after render.",
                    "order_index": 1,
                    "options": [
                        {"option_text": "useState", "is_correct": False, "order_index": 1},
                        {"option_text": "useEffect", "is_correct": True, "order_index": 2},
                        {"option_text": "useMemo", "is_correct": False, "order_index": 3},
                        {"option_text": "useContext", "is_correct": False, "order_index": 4}
                    ]
                },
                {
                    "question_text": "How does FastAPI achieve high performance comparable to NodeJS and Go?",
                    "explanation": "FastAPI is built on Starlette and Uvicorn, leveraging Python's standard async/await syntax on top of an ASGI asynchronous server.",
                    "order_index": 2,
                    "options": [
                        {"option_text": "By compiling Python code into C++ binaries at runtime", "is_correct": False, "order_index": 1},
                        {"option_text": "By running Starlette and async/await event loops over ASGI", "is_correct": True, "order_index": 2},
                        {"option_text": "By disabling request and response validation completely", "is_correct": False, "order_index": 3},
                        {"option_text": "By using Apache Tomcat as its servlet container", "is_correct": False, "order_index": 4}
                    ]
                },
                {
                    "question_text": "What is the primary role of CORS (Cross-Origin Resource Sharing) middleware?",
                    "explanation": "CORS headers instruct the browser whether frontend applications running on a different domain or port are allowed to make requests to the API.",
                    "order_index": 3,
                    "options": [
                        {"option_text": "To compress image assets transferred over HTTP", "is_correct": False, "order_index": 1},
                        {"option_text": "To specify which origin domains are allowed to access API resources", "is_correct": True, "order_index": 2},
                        {"option_text": "To encrypt passwords before writing to the database", "is_correct": False, "order_index": 3},
                        {"option_text": "To restart crashed database servers automatically", "is_correct": False, "order_index": 4}
                    ]
                },
                {
                    "question_text": "Why should password hashing algorithms like bcrypt be used instead of plain SHA-256?",
                    "explanation": "Bcrypt includes a salt and a configurable work factor (slowness) that resists brute-force and GPU rainbow table cracking attacks.",
                    "order_index": 4,
                    "options": [
                        {"option_text": "Bcrypt is reversible so lost passwords can be sent via email", "is_correct": False, "order_index": 1},
                        {"option_text": "Bcrypt incorporates per-user salts and deliberate computational difficulty against brute-force", "is_correct": True, "order_index": 2},
                        {"option_text": "Bcrypt produces shorter strings that save database disk space", "is_correct": False, "order_index": 3},
                        {"option_text": "Bcrypt does not require a secret key", "is_correct": False, "order_index": 4}
                    ]
                }
            ]
        }
    },
    {
        "title": "Cloud Computing, DevOps & Microservices Architecture",
        "slug": "cloud-computing-devops",
        "description": "Learn modern cloud-native architectures, containerization concepts, continuous delivery pipelines, and zero-downtime deployment strategies.",
        "short_description": "Demystify cloud hosting, continuous deployment pipelines, serverless functions, and production reliability.",
        "instructor_name": "Marcus Vance",
        "instructor_role": "Principal DevOps Engineer & Cloud Architect",
        "category": "Cloud & DevOps",
        "difficulty": "Advanced",
        "duration_hours": 11.0,
        "thumbnail_url": "https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=1200&auto=format&fit=crop",
        "modules": [
            {
                "title": "Cloud Service Models (IaaS, PaaS, SaaS)",
                "description": "Compare compute paradigms from bare metal to modern PaaS platforms like Vercel, Render, and Supabase.",
                "learning_objectives": [
                    "Differentiate between Infrastructure, Platform, and Software as a Service",
                    "Select optimal hosting layers based on scale, SLA, and developer velocity",
                    "Understand cloud regions, availability zones, and latency reduction"
                ],
                "resource_title": "NIST Cloud Computing Definition & Architecture Guide",
                "resource_url": "https://csrc.nist.gov/publications/detail/sp/800-145/final",
                "resource_type": "textbook",
                "order_index": 1,
                "is_required": True,
                "estimated_minutes": 35
            },
            {
                "title": "Continuous Integration & Deployment (CI/CD) Pipelines",
                "description": "Automate linting, unit testing, and cloud artifact deployment triggered on every Git push.",
                "learning_objectives": [
                    "Design automated build pipelines for frontend and backend",
                    "Implement semantic versioning and release gating",
                    "Configure rollback strategies for rapid recovery"
                ],
                "resource_title": "GitHub Actions Documentation — CI/CD Core Concepts",
                "resource_url": "https://docs.github.com/en/actions/learn-github-actions/understanding-github-actions",
                "resource_type": "documentation",
                "order_index": 2,
                "is_required": True,
                "estimated_minutes": 40
            },
            {
                "title": "Health Monitoring, Logging & Zero-Downtime Releases",
                "description": "Implement synthetic health check endpoints, structured JSON logging, and connection pooling for production services.",
                "learning_objectives": [
                    "Construct /health endpoints that test database and downstream readiness",
                    "Detect resource leaks and manage connection pooling limits",
                    "Analyze HTTP response codes and synthetic monitoring alarms"
                ],
                "resource_title": "Google SRE Book — Monitoring Distributed Systems",
                "resource_url": "https://sre.google/sre-book/monitoring-distributed-systems/",
                "resource_type": "textbook",
                "order_index": 3,
                "is_required": True,
                "estimated_minutes": 45
            }
        ],
        "quiz": {
            "title": "Cloud Computing & DevOps Mastery Assessment",
            "description": "Verify your understanding of modern continuous deployment pipelines, PaaS hosting architectures, and high-availability operations.",
            "passing_score": 66.0,
            "time_limit_minutes": 10,
            "questions": [
                {
                    "question_text": "What distinguishes PaaS (Platform as a Service) like Render or Vercel from raw IaaS (Infrastructure as a Service)?",
                    "explanation": "PaaS handles operating system patching, runtime maintenance, networking, and scaling automatically, letting developers focus on application code.",
                    "order_index": 1,
                    "options": [
                        {"option_text": "PaaS requires you to install and patch your own Linux kernel", "is_correct": False, "order_index": 1},
                        {"option_text": "PaaS abstracts OS and server maintenance while providing automated git-driven deployments", "is_correct": True, "order_index": 2},
                        {"option_text": "PaaS only supports static HTML websites", "is_correct": False, "order_index": 3},
                        {"option_text": "PaaS cannot connect to external databases", "is_correct": False, "order_index": 4}
                    ]
                },
                {
                    "question_text": "Why is a dedicated /health endpoint standard in cloud hosting environments?",
                    "explanation": "Load balancers and platform orchestrators poll the health endpoint to verify the service is ready to receive traffic and restart dead instances.",
                    "order_index": 2,
                    "options": [
                        {"option_text": "It displays student exam scores publicly", "is_correct": False, "order_index": 1},
                        {"option_text": "Cloud orchestrators probe it to determine container health and routing eligibility", "is_correct": True, "order_index": 2},
                        {"option_text": "It automatically generates SSL certificates", "is_correct": False, "order_index": 3},
                        {"option_text": "It backups database rows to disk every minute", "is_correct": False, "order_index": 4}
                    ]
                },
                {
                    "question_text": "What is the recommended practice for managing database credentials in cloud environments?",
                    "explanation": "Never commit credentials to version control; inject them securely via environment variables at runtime.",
                    "order_index": 3,
                    "options": [
                        {"option_text": "Hardcode credentials directly inside the JavaScript bundle", "is_correct": False, "order_index": 1},
                        {"option_text": "Commit .env files to public GitHub repositories", "is_correct": False, "order_index": 2},
                        {"option_text": "Inject them as runtime environment variables protected by the cloud platform", "is_correct": True, "order_index": 3},
                        {"option_text": "Store passwords in plain text in browser cookies", "is_correct": False, "order_index": 4}
                    ]
                }
            ]
        }
    },
    {
        "title": "Python for Data Science, Algorithms & Machine Learning",
        "slug": "python-data-science-algorithms",
        "description": "Learn applied data science, algorithm analysis, statistical computing, and machine learning pipelines using Python's scientific ecosystem.",
        "short_description": "Explore data manipulation, algorithmic problem solving, statistical modeling, and machine learning pipelines.",
        "instructor_name": "Dr. Ananya Roy",
        "instructor_role": "Data Scientist & AI Research Fellow",
        "category": "Data Science & AI",
        "difficulty": "Beginner",
        "duration_hours": 12.0,
        "thumbnail_url": "https://images.unsplash.com/photo-1555949963-aa79dcee981c?q=80&w=1200&auto=format&fit=crop",
        "modules": [
            {
                "title": "Algorithmic Thinking & Python Data Structures",
                "description": "Master lists, dictionaries, sets, and asymptotic time complexity (Big-O notation).",
                "learning_objectives": [
                    "Analyze algorithms using Big-O time and space complexity",
                    "Select optimal data structures for O(1) hash lookups",
                    "Implement clean list comprehensions and generator expressions"
                ],
                "resource_title": "Python Official Documentation — Data Structures",
                "resource_url": "https://docs.python.org/3/tutorial/datastructures.html",
                "resource_type": "documentation",
                "order_index": 1,
                "is_required": True,
                "estimated_minutes": 40
            },
            {
                "title": "Numerical Computing & Vectorization with NumPy",
                "description": "Understand multi-dimensional arrays, matrix broadcasting, and vectorized computational efficiency.",
                "learning_objectives": [
                    "Manipulate N-dimensional arrays efficiently without explicit loops",
                    "Apply broadcasting rules for matrix arithmetic",
                    "Calculate descriptive statistics across matrix axes"
                ],
                "resource_title": "NumPy User Guide — The Absolute Basics",
                "resource_url": "https://numpy.org/doc/stable/user/absolute_beginners.html",
                "resource_type": "tutorial",
                "order_index": 2,
                "is_required": True,
                "estimated_minutes": 45
            },
            {
                "title": "Machine Learning Workflows & Model Evaluation",
                "description": "Explore train/test splits, supervised classification metrics (precision, recall, F1 score), and bias-variance trade-offs.",
                "learning_objectives": [
                    "Formulate supervised learning problems cleanly",
                    "Prevent data leakage using train-test separation",
                    "Evaluate predictive performance using confusion matrices and ROC curves"
                ],
                "resource_title": "Scikit-Learn Machine Learning Supervised Learning Guide",
                "resource_url": "https://scikit-learn.org/stable/supervised_learning.html",
                "resource_type": "guide",
                "order_index": 3,
                "is_required": True,
                "estimated_minutes": 50
            }
        ],
        "quiz": {
            "title": "Python & Data Science Core Concepts Quiz",
            "description": "Evaluate your understanding of Python data structures, vectorization benefits, and machine learning validation methodology.",
            "passing_score": 66.0,
            "time_limit_minutes": 10,
            "questions": [
                {
                    "question_text": "What is the average time complexity for key lookup in a Python dictionary?",
                    "explanation": "Python dictionaries are implemented as hash tables with an average O(1) constant time lookup.",
                    "order_index": 1,
                    "options": [
                        {"option_text": "O(N)", "is_correct": False, "order_index": 1},
                        {"option_text": "O(1)", "is_correct": True, "order_index": 2},
                        {"option_text": "O(log N)", "is_correct": False, "order_index": 3},
                        {"option_text": "O(N^2)", "is_correct": False, "order_index": 4}
                    ]
                },
                {
                    "question_text": "Why is vectorization in NumPy significantly faster than native Python for-loops?",
                    "explanation": "NumPy arrays are contiguous blocks of memory processed via compiled C loops and SIMD hardware instructions.",
                    "order_index": 2,
                    "options": [
                        {"option_text": "NumPy skips mathematical calculations entirely", "is_correct": False, "order_index": 1},
                        {"option_text": "Vectorized operations run in compiled C with contiguous memory and SIMD acceleration", "is_correct": True, "order_index": 2},
                        {"option_text": "NumPy relies on local browser storage for caching numbers", "is_correct": False, "order_index": 3},
                        {"option_text": "Python for-loops are not supported in 64-bit operating systems", "is_correct": False, "order_index": 4}
                    ]
                },
                {
                    "question_text": "What is the primary danger of evaluating a machine learning model on the same data used for training?",
                    "explanation": "Evaluating on training data hides overfitting because the model may simply memorize data rather than generalizing to unseen samples.",
                    "order_index": 3,
                    "options": [
                        {"option_text": "The computer CPU will immediately overheat", "is_correct": False, "order_index": 1},
                        {"option_text": "Overfitting will not be detected, giving a false illusion of generalization capability", "is_correct": True, "order_index": 2},
                        {"option_text": "The model will automatically delete the database", "is_correct": False, "order_index": 3},
                        {"option_text": "Training data always contains negative numbers", "is_correct": False, "order_index": 4}
                    ]
                }
            ]
        }
    }
]

def seed_database(db: Session):
    # Check if courses already exist
    existing_count = db.query(Course).count()
    if existing_count > 0:
        return

    # Seed demo student account for instant faculty evaluation
    demo_user = db.query(User).filter(
        (User.email == "student@nextlearn.edu") | (User.email == "student@learnhub.edu")
    ).first()
    if not demo_user:
        demo_user = User(
            email="student@nextlearn.edu",
            hashed_password=get_password_hash("NextLearn2025!"),
            full_name="Alex Mercer (Demo Student)",
            role="student"
        )
        db.add(demo_user)
        db.commit()


    for c_data in SAMPLE_COURSES:
        course = Course(
            title=c_data["title"],
            slug=c_data["slug"],
            description=c_data["description"],
            short_description=c_data["short_description"],
            instructor_name=c_data["instructor_name"],
            instructor_role=c_data["instructor_role"],
            category=c_data["category"],
            difficulty=c_data["difficulty"],
            duration_hours=c_data["duration_hours"],
            thumbnail_url=c_data["thumbnail_url"],
            is_published=True
        )
        db.add(course)
        db.flush()

        for m_data in c_data["modules"]:
            module = Module(
                course_id=course.id,
                title=m_data["title"],
                description=m_data["description"],
                learning_objectives=m_data["learning_objectives"],
                resource_title=m_data["resource_title"],
                resource_url=m_data["resource_url"],
                resource_type=m_data["resource_type"],
                order_index=m_data["order_index"],
                is_required=m_data["is_required"],
                estimated_minutes=m_data["estimated_minutes"]
            )
            db.add(module)

        q_data = c_data["quiz"]
        quiz = Quiz(
            course_id=course.id,
            title=q_data["title"],
            description=q_data["description"],
            passing_score=q_data["passing_score"],
            time_limit_minutes=q_data["time_limit_minutes"]
        )
        db.add(quiz)
        db.flush()

        for q_item in q_data["questions"]:
            question = QuizQuestion(
                quiz_id=quiz.id,
                question_text=q_item["question_text"],
                explanation=q_item["explanation"],
                order_index=q_item["order_index"]
            )
            db.add(question)
            db.flush()

            for opt_item in q_item["options"]:
                option = QuizOption(
                    question_id=question.id,
                    option_text=opt_item["option_text"],
                    is_correct=opt_item["is_correct"],
                    order_index=opt_item["order_index"]
                )
                db.add(option)

    db.commit()
