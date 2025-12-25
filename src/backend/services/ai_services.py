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


def classify_video(video_url):
    client = genai.Client(api_key=settings.google_api_key)
    video_bytes = requests.get(video_url).content
    
    prompt = """
    You are given a video. Classify it into exactly one of the following categories:

    0 — Review Food Store: reviews or rates a restaurant, cafe, or food shop.
    1 — Cooking Guide: teaches or demonstrates how to cook or prepare food.
    2 — Mukbang: features someone eating large amounts of food, often while interacting with the audience.

    Respond with only the integer 0, 1, or 2 corresponding to the correct category. Do not include any other text.
    """

    response = client.models.generate_content(
        model="gemini-1.5-pro",
        contents=types.Content(
            parts=[
                types.Part(
                    inline_data=types.Blob(data=video_bytes, mime_type='video/mp4')
                ),
                types.Part(text=prompt)
            ]
        ),
        config={
            "response_mime_type": "application/json",
            "response_schema": int,
        }
    )

    print(response.text)
    return int(response.text)


def summarize_video(type, video_url):
    if type == 0:
        schema = list[Restaurant]
        text_prompt = "Đây là một video về review những quán ăn, hãy trích xuất những thông tin về một hoặc nhiều quán ăn được nhắc đến trong video. Hãy trả lời bằng Tiếng Việt"
    else:
        schema = CookingGuide
        text_prompt = "Đây là một video về hướng dẫn nấu ăn. Hãy trích xuất những thông tin về món ăn và cách nấu: Tên món ăn, miêu tả món ăn, nguyên liệu, các bước nấu ăn. Hãy trả lời bằng Tiếng Việt"

    
    print(schema)
    client = genai.Client(api_key=settings.google_api_key)
    # video_url = "https://fgkmsasdgcykscfcsynx.supabase.co/storage/v1/object/public/videobucket/@dianthoii__video_7474461317957520658.mp4"
    video_bytes = requests.get(video_url).content
    response = client.models.generate_content(
        model="gemini-1.5-pro",
        contents=types.Content(
        parts=[
            types.Part(
                inline_data=types.Blob(data=video_bytes, mime_type='video/mp4')
            ),
            types.Part(text=text_prompt)
        ]
        ),
        config={
        "response_mime_type": "application/json",
        "response_schema": schema,
        }
    )
    # print(response.text)
    # get_distance("89-91 Nguyen Gia Tri, Binh Thanh")
    # print(recommend_dish(user_id = "cbcf5839-9c3f-499a-b4a6-3302f734776c", user_lat=10, user_lng=104))
    return response.text