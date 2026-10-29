@echo off
title NextLearn FastAPI Backend
echo ========================================================
echo Starting NextLearn Python FastAPI Backend on Port 8000...
echo ========================================================

cd /d "%~dp0backend"
if exist ".venv\Scripts\python.exe" (
    .venv\Scripts\python.exe -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
) else (
    python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
)
pause
