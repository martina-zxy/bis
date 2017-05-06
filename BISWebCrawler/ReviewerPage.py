from bs4 import BeautifulSoup
import requests
import time
from entity.book import Book
from entity.user import Author
from entity.user import Reviewer
import json
import sys

url = 'https://www.amazon.com/gp/pdp/profile/A22E3TNWVXU7Q0/ref=cm_cr_dp_d_pdp?ie=UTF8'



def get_reviewer(url):
    #max page size = 50
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows; U; Win98; en-US; rv:1.8.1.8pre) Gecko/20070928 Firefox/2.0.0.7 Navigator/9.0RC1',
        # 'User-Agent': 'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533+ (KHTML, like Gecko) Element Browser 5.0',
        # 'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko',
        'Connection': 'keep-alive',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
    }

    idx_start_reviewer_id = url.find('profile/')
    str_url_temp = url[idx_start_reviewer_id + 8 : ]
    idx_end_reviewer_id = str_url_temp.find('/')
    url = url[:idx_start_reviewer_id + 8 + idx_end_reviewer_id + 1]
    print(url)
    # sys.exit()

    wb_data = requests.get(url, headers=headers)
    print(wb_data.text)
    if wb_data.status_code != 200:
        return None
    # print(wb_data.text)
    time.sleep(1)
    soup = BeautifulSoup(wb_data.text,'lxml')

    body = soup.select('.body');
    if len(body) < 1:
        print(soup.prettify())
    data_raw = body[0].find('div')._attr_value_as_string('data');
    data = json.loads(data_raw)
    # print(data)
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
            if nb_following > 0:
                following_url = "https://www.amazon.com" + stat['url']

    if 'k' in nb_helpful:
        nb_helpful = nb_helpful.replace('k','')
        nb_helpful = float(nb_helpful) * 1000

    following = []
    if nb_following > 0:
        following_data = requests.get(following_url, headers=headers)
        # print(following_data.text)
        following_soup = BeautifulSoup(following_data.text,'lxml')
        following_rows = following_soup.find_all('div', class_ = "pr-follows-row")
        for row in following_rows:
            followed_type = row.find('span', class_ = "pr-follows-popular-items").text.replace("...","").strip()

            type = 'author'
            if followed_type == "Amazon Customer" :
                type = 'customer'
            else:
                idx_colon = followed_type.find(':')
                if idx_colon < 0:
                    continue

            # print(followed_type)
            author_soup = row.find('a')
            author_name = author_soup.get('title')
            author_link = 'https://www.amazon.com' + author_soup.get('href')

            author_id = None
            # idx_start_id = author_link.find("/gp/profile/")
            # if idx_start_id > 0:
            #     str_temp_id = author_link[idx_start_id+12:]
            #     idx_separator = str_temp_id.find('/')
            #     author_id = str_temp_id[:idx_separator]
            # else :
            #     idx_start_id = author_link.find("/e/")
            #     str_temp_id = author_link[idx_start_id+3 : ]
            #     idx_end_id = str_temp_id.find("/")
            #     author_id = str_temp_id[:idx_end_id]

            # print(author_id)
            author = Author(author_id)
            author.name = author_name
            author.link = author_link
            author.type = type

            idx_start_author_id = author_link.find("follows")
            if idx_start_author_id > 0 :
                idx_start_author_id = idx_start_author_id + 8
                author_id_temp = author_link[idx_start_author_id:]
                idx_end_author_id = author_id_temp.find('?')
                author.id = author_id_temp[:idx_end_author_id]
            else :
                idx_start_author_id = author_link.find("/e/") + 3
                author_id_temp = author_link[idx_start_author_id:]
                idx_end_author_id = author_id_temp.find('/')
                author.id = author_id_temp[:idx_end_author_id]

            following.append(author)

    rank = 0
    rankStr = data['bioData']['topReviewerInfo']['rank']
    if rankStr is not None :
        rank = rankStr.replace(",","")
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

get_reviewer(url);
