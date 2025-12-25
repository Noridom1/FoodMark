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