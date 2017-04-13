from bs4 import BeautifulSoup
import requests
import time
from entity.book import Book
from entity.user import Author
from entity.user import Reviewer
import json

url = 'https://www.amazon.com/gp/profile/amzn1.account.AH5KNRSD6RKBCN4F7WT6TPIYEUGA'

def get_reviewer(url,data=None):
    #max page size = 50
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36',
        'Connection': 'keep-alive',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
    }

    wb_data = requests.get(url, headers=headers)
    # print(wb_data.text)
    time.sleep(1)
    soup = BeautifulSoup(wb_data.text,'lxml')

    body = soup.select('.body');
    data_raw = body[0].find('div')._attr_value_as_string('data');
    data = json.loads(data_raw)
    print(data)
    id = data['customerId']
    name = data['nameHeaderData']['name'].strip().replace('\'','\'\'')

    if len(data['bioData']['occupationLocationList']) > 0:
        location_occupation = data['bioData']['occupationLocationList'][0]
    else:
        location_occupation = ''

    if data['bioData']['personalDescription'] is None:
        profile = ''
    else:
        profile = data['bioData']['personalDescription'].strip().replace('\'','\'\'')

    nb_helpful = ''
    nb_following = 0
    following_url = ''
    stats = data['statsBarData']['stats']
    for stat in stats:
        if stat['name'] == 'helpful_votes':
            nb_helpful = stat['value']
        elif stat['name'] == 'following':
            nb_following = int(stat['value'])
            following_url = "https://www.amazon.com" + stat['url']

    if 'k' in nb_helpful:
        nb_helpful = nb_helpful.replace('k','')
        nb_helpful = float(nb_helpful) * 1000

    if nb_following > 0:
        following = []
        following_data = requests.get(following_url, headers=headers)
        # print(following_data.text)
        following_soup = BeautifulSoup(following_data.text,'lxml')
        following_rows = following_soup.find_all('div', class_ = "pr-follows-row")
        for row in following_rows:
            author_soup = row.find('a')
            author_name = author_soup.get('title')
            author_link = author_soup.get('href')

            str_temp_id = author_link.replace("/gp/profile/", "")
            idx_separator = str_temp_id.find('/')
            author_id = str_temp_id[:idx_separator]
            author = Author(author_id)
            author.name = author_name
            author.link = author_link
            following.append(author)

    rank = data['bioData']['topReviewerInfo']['rank'].replace(",","")
    rank = int(rank)

    print()
    # print(trace[4].prettify())
    # print(name)
    # print(location_occupation)
    # print(profile)
    # print(nb_helpful)
    # print(rank)

    reviewer = Reviewer(id);
    reviewer.name = name
    reviewer.country = location_occupation
    reviewer.profileText = profile
    reviewer.nbHelpfulVotes = nb_helpful
    reviewer.rank = rank
    reviewer.following = following

    return reviewer
    # there is no way to check if the user uses a real profile picture only from the link

# get_reviewer(url);
