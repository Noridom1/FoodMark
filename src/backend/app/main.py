import os
import torch
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from loguru import logger

# from backend.routers import (
#     auth, user
# )


@asynccontextmanager
async def lifespan(app: FastAPI):
    
    yield
    logger.info("Shutting down app")



def create_app():
    app = FastAPI(title="FoodMark App", version="1.0.0", lifespan=lifespan)

    allowed = os.getenv("ALLOWED_ORIGINS", "http://localhost:5173").split(",")
    app.add_middleware(
        CORSMiddleware,
        allow_origins=[o.strip() for o in allowed],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    
    # app.include_router(auth.router)
    # app.include_router(user.router)
    
    return app


app = create_app()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000, reload=True)