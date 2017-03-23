from bs4 import BeautifulSoup
import requests
import time
from entity.book import Book
from entity.user import Reviewer

url = 'https://www.amazon.com/gp/profile/amzn1.account.AH5KNRSD6RKBCN4F7WT6TPIYEUGA'

def get_reviewer(url,data=None):
    #max page size = 50
    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' #don't remove!
    }

    wb_data = requests.get(url, headers=headers)
    print(wb_data.status_code)
    print(wb_data.text)
    time.sleep(1)
    soup = BeautifulSoup(wb_data.text,'lxml')

get_reviewer(url);