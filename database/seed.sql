-- ==============================================================================
-- LearnHub Database Initial Seed SQL (Supabase PostgreSQL Compatible)
-- Populates courses, modules with official educational resources, and quizzes
-- ==============================================================================

-- 1. Insert Courses
INSERT INTO courses (id, title, slug, description, short_description, instructor_name, instructor_role, category, difficulty, duration_hours, thumbnail_url, is_published)
VALUES 
(1, 'Full-Stack Web Development with React & FastAPI', 'full-stack-react-fastapi', 
 'Master end-to-end full stack web architecture. Learn how modern single-page applications built with React interact with high-performance Python FastAPI backends, secure JWT authentication, and cloud databases.',
 'Build production-grade web applications with React, Vite, Tailwind CSS, Python FastAPI, and PostgreSQL.',
 'Dr. Sarah Jenkins', 'Lead Software Architect & University Lecturer', 'Web Development', 'Intermediate', 14.5,
 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?q=80&w=1200&auto=format&fit=crop', TRUE),

(2, 'Cloud Computing, DevOps & Microservices Architecture', 'cloud-computing-devops',
 'Learn modern cloud-native architectures, containerization concepts, continuous delivery pipelines, and zero-downtime deployment strategies.',
 'Demystify cloud hosting, continuous deployment pipelines, serverless functions, and production reliability.',
 'Marcus Vance', 'Principal DevOps Engineer & Cloud Architect', 'Cloud & DevOps', 'Advanced', 11.0,
 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=1200&auto=format&fit=crop', TRUE),

(3, 'Python for Data Science, Algorithms & Machine Learning', 'python-data-science-algorithms',
 'Learn applied data science, algorithm analysis, statistical computing, and machine learning pipelines using Python''s scientific ecosystem.',
 'Explore data manipulation, algorithmic problem solving, statistical modeling, and machine learning pipelines.',
 'Dr. Ananya Roy', 'Data Scientist & AI Research Fellow', 'Data Science & AI', 'Beginner', 12.0,
 'https://images.unsplash.com/photo-1555949963-aa79dcee981c?q=80&w=1200&auto=format&fit=crop', TRUE)
ON CONFLICT (id) DO NOTHING;

-- 2. Insert Modules for Course 1
INSERT INTO modules (id, course_id, title, description, learning_objectives, resource_title, resource_url, resource_type, order_index, is_required, estimated_minutes)
VALUES
(1, 1, 'Modern Frontend Fundamentals with React & Vite', 
 'Understand component hierarchy, state management with hooks (useState, useEffect), and responsive layout styling with Tailwind CSS.',
 '["Set up blazing-fast React projects using Vite", "Manage state and side-effects efficiently using React hooks", "Build reusable, accessible UI component hierarchies"]'::jsonb,
 'Official React Documentation — Describing the UI', 'https://react.dev/learn/describing-the-ui', 'documentation', 1, TRUE, 45),

(2, 1, 'High-Performance REST APIs with FastAPI',
 'Design asynchronous REST endpoints, utilize Pydantic schemas for data validation, and explore automatic OpenAPI documentation.',
 '["Create asynchronous endpoints with Python FastAPI", "Validate incoming requests and outgoing payloads with Pydantic v2", "Inspect and test endpoints using interactive Swagger docs"]'::jsonb,
 'FastAPI Official Tutorial — First Steps', 'https://fastapi.tiangolo.com/tutorial/first-steps/', 'documentation', 2, TRUE, 40),

(3, 1, 'Cloud Database Architecture & SQLAlchemy ORM',
 'Connect FastAPI to cloud PostgreSQL, define relational models, execute migrations, and implement robust query sessions.',
 '["Design scalable database schemas with foreign keys and unique constraints", "Prevent SQL injection using SQLAlchemy 2.0 ORM query building", "Handle database connection pooling and failovers safely"]'::jsonb,
 'SQLAlchemy 2.0 Unified Tutorial', 'https://docs.sqlalchemy.org/en/20/tutorial/index.html', 'textbook', 3, TRUE, 50),

(4, 1, 'Production Deployment & Security Best Practices',
 'Prepare production builds, configure CORS for cross-origin domain isolation, manage environment variables, and deploy to Vercel and cloud platforms.',
 '["Enforce CORS origins and prevent unauthorized API access", "Safely store credentials via environment variables without git leaks", "Configure single-page application routing redirects on static hosts"]'::jsonb,
 'OWASP Web Security Testing Guide — Authentication', 'https://owasp.org/www-project-web-security-testing-guide/latest/4-Web_Application_Security_Testing/04-Authentication_Testing/', 'guide', 4, TRUE, 35)
ON CONFLICT (id) DO NOTHING;

-- 3. Insert Modules for Course 2
INSERT INTO modules (id, course_id, title, description, learning_objectives, resource_title, resource_url, resource_type, order_index, is_required, estimated_minutes)
VALUES
(5, 2, 'Cloud Service Models (IaaS, PaaS, SaaS)',
 'Compare compute paradigms from bare metal to modern PaaS platforms like Vercel, Render, and Supabase.',
 '["Differentiate between Infrastructure, Platform, and Software as a Service", "Select optimal hosting layers based on scale, SLA, and developer velocity", "Understand cloud regions, availability zones, and latency reduction"]'::jsonb,
 'NIST Cloud Computing Definition & Architecture Guide', 'https://csrc.nist.gov/publications/detail/sp/800-145/final', 'textbook', 1, TRUE, 35),

(6, 2, 'Continuous Integration & Deployment (CI/CD) Pipelines',
 'Automate linting, unit testing, and cloud artifact deployment triggered on every Git push.',
 '["Design automated build pipelines for frontend and backend", "Implement semantic versioning and release gating", "Configure rollback strategies for rapid recovery"]'::jsonb,
 'GitHub Actions Documentation — CI/CD Core Concepts', 'https://docs.github.com/en/actions/learn-github-actions/understanding-github-actions', 'documentation', 2, TRUE, 40),

(7, 2, 'Health Monitoring, Logging & Zero-Downtime Releases',
 'Implement synthetic health check endpoints, structured JSON logging, and connection pooling for production services.',
 '["Construct /health endpoints that test database and downstream readiness", "Detect resource leaks and manage connection pooling limits", "Analyze HTTP response codes and synthetic monitoring alarms"]'::jsonb,
 'Google SRE Book — Monitoring Distributed Systems', 'https://sre.google/sre-book/monitoring-distributed-systems/', 'textbook', 3, TRUE, 45)
ON CONFLICT (id) DO NOTHING;

-- 4. Insert Modules for Course 3
INSERT INTO modules (id, course_id, title, description, learning_objectives, resource_title, resource_url, resource_type, order_index, is_required, estimated_minutes)
VALUES
(8, 3, 'Algorithmic Thinking & Python Data Structures',
 'Master lists, dictionaries, sets, and asymptotic time complexity (Big-O notation).',
 '["Analyze algorithms using Big-O time and space complexity", "Select optimal data structures for O(1) hash lookups", "Implement clean list comprehensions and generator expressions"]'::jsonb,
 'Python Official Documentation — Data Structures', 'https://docs.python.org/3/tutorial/datastructures.html', 'documentation', 1, TRUE, 40),

(9, 3, 'Numerical Computing & Vectorization with NumPy',
 'Understand multi-dimensional arrays, matrix broadcasting, and vectorized computational efficiency.',
 '["Manipulate N-dimensional arrays efficiently without explicit loops", "Apply broadcasting rules for matrix arithmetic", "Calculate descriptive statistics across matrix axes"]'::jsonb,
 'NumPy User Guide — The Absolute Basics', 'https://numpy.org/doc/stable/user/absolute_beginners.html', 'tutorial', 2, TRUE, 45),

(10, 3, 'Machine Learning Workflows & Model Evaluation',
 'Explore train/test splits, supervised classification metrics (precision, recall, F1 score), and bias-variance trade-offs.',
 '["Formulate supervised learning problems cleanly", "Prevent data leakage using train-test separation", "Evaluate predictive performance using confusion matrices and ROC curves"]'::jsonb,
 'Scikit-Learn Machine Learning Supervised Learning Guide', 'https://scikit-learn.org/stable/supervised_learning.html', 'guide', 3, TRUE, 50)
ON CONFLICT (id) DO NOTHING;

-- 5. Insert Quizzes
INSERT INTO quizzes (id, course_id, title, description, passing_score, time_limit_minutes)
VALUES
(1, 1, 'Full-Stack React & FastAPI Certification Quiz', 'Test your comprehensive understanding of React frontend state, FastAPI request lifecycles, and secure database integrations.', 75.0, 15),
(2, 2, 'Cloud Computing & DevOps Mastery Assessment', 'Verify your understanding of modern continuous deployment pipelines, PaaS hosting architectures, and high-availability operations.', 66.0, 10),
(3, 3, 'Python & Data Science Core Concepts Quiz', 'Evaluate your understanding of Python data structures, vectorization benefits, and machine learning validation methodology.', 66.0, 10)
ON CONFLICT (id) DO NOTHING;

-- 6. Insert Quiz Questions for Course 1
INSERT INTO quiz_questions (id, quiz_id, question_text, explanation, order_index)
VALUES
(1, 1, 'Which React hook is used to perform side-effects such as fetching data from an external API after rendering?', 'useEffect is specifically designed for side effects like API calls, event listeners, and DOM mutations after render.', 1),
(2, 1, 'How does FastAPI achieve high performance comparable to NodeJS and Go?', 'FastAPI is built on Starlette and Uvicorn, leveraging Python''s standard async/await syntax on top of an ASGI asynchronous server.', 2),
(3, 1, 'What is the primary role of CORS (Cross-Origin Resource Sharing) middleware?', 'CORS headers instruct the browser whether frontend applications running on a different domain or port are allowed to make requests to the API.', 3),
(4, 1, 'Why should password hashing algorithms like bcrypt be used instead of plain SHA-256?', 'Bcrypt includes a salt and a configurable work factor (slowness) that resists brute-force and GPU rainbow table cracking attacks.', 4)
ON CONFLICT (id) DO NOTHING;

-- 7. Insert Quiz Options for Course 1
INSERT INTO quiz_options (id, question_id, option_text, is_correct, order_index)
VALUES
(1, 1, 'useState', FALSE, 1),
(2, 1, 'useEffect', TRUE, 2),
(3, 1, 'useMemo', FALSE, 3),
(4, 1, 'useContext', FALSE, 4),

(5, 2, 'By compiling Python code into C++ binaries at runtime', FALSE, 1),
(6, 2, 'By running Starlette and async/await event loops over ASGI', TRUE, 2),
(7, 2, 'By disabling request and response validation completely', FALSE, 3),
(8, 2, 'By using Apache Tomcat as its servlet container', FALSE, 4),

(9, 3, 'To compress image assets transferred over HTTP', FALSE, 1),
(10, 3, 'To specify which origin domains are allowed to access API resources', TRUE, 2),
(11, 3, 'To encrypt passwords before writing to the database', FALSE, 3),
(12, 3, 'To restart crashed database servers automatically', FALSE, 4),

(13, 4, 'Bcrypt is reversible so lost passwords can be sent via email', FALSE, 1),
(14, 4, 'Bcrypt incorporates per-user salts and deliberate computational difficulty against brute-force', TRUE, 2),
(15, 4, 'Bcrypt produces shorter strings that save database disk space', FALSE, 3),
(16, 4, 'Bcrypt does not require a secret key', FALSE, 4)
ON CONFLICT (id) DO NOTHING;
