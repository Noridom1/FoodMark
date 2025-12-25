from google import genai
import requests
from google.genai import types
from ..config import settings
from pydantic import BaseModel
from typing import List, Optional
import requests
from ..database import supabase
from geopy.distance import geodesic

from google.genai.errors import ServerError
from tenacity import retry, stop_after_attempt, wait_exponential, retry_if_exception_type

class Dish(BaseModel):
    name: str
    price: str
    rating: Optional[float] = None   # e.g. 4.5
    taste: Optional[str] = None      # e.g. "spicy", "sweet and sour"

class Review(BaseModel):
    title: str
    rating: float                    # rating for the whole restaurant experience
    price: Optional[str] = None      # overall price range, e.g. "$$"
    comment: Optional[str] = None    # optional review text


class Restaurant(BaseModel):
    name: str
    address: Optional[str] = None
    dishes: List[Dish] = []
    reviews: List[Review] = []

class CookingStep(BaseModel):
    step_number: int
    title: str
    instruction: str


class CookingGuide(BaseModel):
    title: str
    summary: Optional[str] = None
    ingredients: List[str]
    steps: List[CookingStep]
 

class DishRecommendation(BaseModel):
    id: str
    name: str
    price: Optional[str] = None
    rating: Optional[float] = None
    taste: Optional[str] = None

class StoreRecommendation(BaseModel):
    id: str
    name: str
    address: str
    lat: float
    lng: float
    distance_km: float
    recommended_dishes: List[DishRecommendation]

class RouteRecommendation(BaseModel):
    route: List[StoreRecommendation]
    description: str

