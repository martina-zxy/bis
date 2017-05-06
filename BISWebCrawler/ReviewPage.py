from bs4 import BeautifulSoup
import requests
import time
from entity.book import Book
from entity.book import Review
from entity.book import Comment
from entity.user import Author
import re
from datetime import datetime
import json

headers = {
    # 'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko',
    'User-Agent': 'Mozilla/5.0 (Windows; U; Win98; en-US; rv:1.8.1.8pre) Gecko/20070928 Firefox/2.0.0.7 Navigator/9.0RC1',
    'Connection': 'keep-alive',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
    # 'Cookie': 'aws-target-static-id=1489245108811-141740; aws-session-id=156-4065030-0477716; aws-session-id-time=2119965116l; __utmz=194891197.1489245117.1.1.utmccn=(referral)|utmcsr=portal.aws.amazon.com|utmcct=/billing/signup|utmcmd=referral; aws-business-metrics-last-visit=1489245154635; at-main=Atza|IwEBIHSbBpLorVVuewZnDIZ0aHAk7gCjO2MNrL_J0eKo1qjt5DImSZ1_e_SJzVA_IJUDqji7LqPj2QtmhVOOui63_MB1hXEvhCBo_NXJQJo2bjeqNb1AahoT_NHMoYKjcBegQtVk3df93HT6erZC_2MQ-yzCm0ILv4FSG2qwV2Ph-OQu_HxmBrjE_DiQWI2RQbsX_FGoO0dbcAhN4mflXKDm8AnH802H69kesHfnObykuAoDEUA-Xp8wrYfoe6DPVF6K-UAWg57VNa0z6co0SpM-hdDaNgmZmYKmms7lfjI0YR2WvZM38F_kMZJblOUDcFiQxVXr0Zw2nFb2W9WtN03I20vpbOz7t28VcE90lMRByJn4KrkVLN6lIcFttmI-KxsrRn7bzxlC6TSFT7mAt-B2VoGT; s_pers=%20s_nr%3D1489246399697-New%7C1497022399697%3B; aws-ubid-main=167-8061496-9307626; aws-session-token="B9IKQJ8DgUo7/RD2wFnPePkChgBfKfp88Wxi/aDugtCQs5uTOlAOyqi+LZwLd5dUtnEDP4/UQ7z1o8rmZeSUmTfZVP4Mmd4dYi0sCWkE8zcK1WCQrmBN/La/ksYjiKiuHpZVvMzLmCMYdvc6Tk30TKTlUAs037A5lVrzNDRI+70WNZTY3NQ6lsNvm8cnXSeFbEQ8igrlXtTSsnQ2tcMHUl4Tlmcsp9oWvv+LM1/WHig="; aws-x-main="3BFjf4BvmfbKgpC@K9kqtJymppleuwmKu8VShIWyYpTAtxDHO7weD@zO7k6LqI?7"; aws-at-main=Atza|IwEBIFsxs2wPNih_InYAqlUSIciX6jd1SZi9OixDqjB-SFIQb8ZJY10bUp7fDfPFKoUSAhObKoP6Y_5fB_EZvtaunqx2AQdEUXfpZo0Pzdsb2YhC6rTC2mxqP4qMerJmXIQOKX3r7xCSMHluM6QuRUwV11RsjzSLWKjxe_Ge_5oFbFroqtPE30xihw5lQ80OhSIXmU7UPVaaPstEw2MQVMtYs7yBB-dlvCSCqoYQ4PcxBKlE_WaGJFx3ZhaYbnhqZoYzgkXd3lseiaJeAPHbVweuToaaODZOeECu2ntopN1ha6IJerGA-xOo5ViU9-0Hwwwgz--t7GGxd3zYXe8TTNssSvjbU64KAQU43ZFLxhn3rhizuS6KA93puWqms7uLo6PIXPmosy96w4k-fZIF3z8TmpzCEGrL_-Cwn55uG8EtuUbzWg; aws-userInfo=%7B%22arn%22%3A%22arn%3Aaws%3Aiam%3A%3A239407844960%3Aroot%22%2C%22alias%22%3A%22%22%2C%22username%22%3A%22Martina%2520Megasari%22%2C%22keybase%22%3A%22t%2FiDMMp5mH2FCW%2FyDfZPVxwchhtfw72RhtpTSPoJIZA%5Cu003d%22%2C%22issuer%22%3A%22https%3A%2F%2Fwww.amazon.com%2Fap%2Fsignin%22%7D; __utmv=194891197.%223BFjf4BvmfbKgpC%40K9kqtJymppleuwmKu8VShIWyYpTAtxDHO7weD%40zO7k6LqI%3F7%22; __utma=194891197.1182645527.1489245117.1489245117.1489247733.2; x-wl-uid=1DjBuj9VVmpXjGXmdVkEVcHODuJdefI1T6L1UYP0qk9ui9jMqvuhPBZYvelUO0eCOJm7EasnyE1iwf994H9TTjui//aV3zczmiFb9d6bnm8xySZorrwLxO6+uc/IeB/2fcdUkc/JOpTs=; aws-target-visitor-id=1489245108821-964344.26_9; aws-target-data=%7B%22support%22%3A%221%22%7D; s_fid=36C1E738A6DD20A0-302516AC101FAA34; s_vn=1520780379735%26vn%3D3; regStatus=registered; x-main="FNIGhPmbd0oaxCBt6X6pgyCZNN?Fq6AbWBNeE96SR90SM1y5eLGly05t3Vv5w6VE"; lc-main=en_US; UserPref=XXUPcxa+FV+O7fDvfvVUk3uLGYKO86S924fjnBybPlUXwKJuRPi+Dk0QzrRdHj2HZRo+vp0j/s3coZaepgCwUs9z8XIK7NyUXzL/USVhYrRBZxbKGYfBinDQ45Yo8zrV65O6hhCYUtTKjEU16oizGH8ekTlXxfIA17xrB+ajg7nt9O2BWpwc1kN1JXGJQVHmG0zYHAr5yBnADKxNhzOJnii2q+s+CLudTTyAMp+aCW3gtfa1Fpb5x58EVv3sgqunUqPbhdleFbLDkssd31OMlQ9ex04sO0ISsqPmDLY4S2AGSGev4VpPgoL7yTNd1ePtaLbgZx9pMPEJQ9YILn5QAWpjTM5y46hN4PFiG6EZ3MJk8I3NKs3AJVsEeCBpI3v7QMnMRrlI4Yhc7PWIE2dShcg5vjaWjA3Zo1Htugxj71gcUJRysR01b4ChVQkfaNrm; s_vnum=1922455017278%26vn%3D2; s_cc=true; s_nr=1490898077684-Repeat; s_dslv=1490898077687; s_sq=%5B%5BB%5D%5D; s_ppv=63; session-token="MCnZ7mg+vJHITDmnIEQ+5YgjVXOa/ziJ/RfaPXE13HfZlWpJu9LXslKZ099AsKQvO9iAh42xRKq7twXk939hwB8KSwSNw4VP1nRPGmihzMqymXpO2p2HeSBzuOQuL3OAZI7sk8seog7obynJJkCZD8mB/ShoZxr3DvhHQTXcZQHBaaQVL0vNQhYUpLWCszjN6MkHAlp/stsH012cKzgTqun3q01/YJqqTaqr9j3wvmUlv0jh3YAZ3ni6sSG43T4WL7wFxJPQRKnGDyomK7C3/Q=="; ubid-main=154-3030463-1341322; session-id-time=2082787201l; session-id=152-8165811-0496857; csm-hit=M9G564RTVHW5NNVVZEWW+s-M9G564RTVHW5NNVVZEWW|1490941393593'
    # 'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' # don't remove!
}


def get_comments(soup):
    list_comments = []
    comments = soup.find_all("div", class_="postBody")
    for com in comments:
        comment = Comment()
        header = com.find("div", class_="postHeader").text.strip()

        idx_enter = header.find('\n')
        if header[idx_enter+1] == '\n':
            idx_enter += 1
        date = header[idx_enter + 1:]

        idx_2nd_enter = date.find('\n')
        if idx_2nd_enter > 0:
            date = date[:idx_2nd_enter]
        date = datetime.strptime(date[:len(date) - 3], '%b %d, %Y, %I:%M:%S %p ')

        commenter = com.find("div", class_="postFrom")
        if commenter is not None :
            commenter_name = commenter.text.strip().replace(" says:", "")
            commenter_link = commenter.find('a').get('href')

            str_temp_id = commenter_link.replace("https://www.amazon.com/gp/pdp/profile/", "")
            idx_separator = str_temp_id.find('/')
            commenter_id = str_temp_id[:idx_separator]

            content = com.find("div", class_="postContent")
            content_text = content.text.strip()
            comment_id = content.get('id').replace("cdPostContentBox_", "")
            if comment_id.find("postHidden") >= 0:
                continue
            comment.date = date
            comment.commenterName = commenter_name
            comment.commenterLink = commenter_link
            comment.commenterId = commenter_id
            comment.id = comment_id
            comment.text = content_text

            list_comments.append(comment)
    # print()
    return list_comments

# list_comment = []
# url_review = "https://www.amazon.com/gp/customer-reviews/R37GHVX44L0OCC/ref=cm_cr_dp_d_rvw_ttl?ie=UTF8&ASIN=0545010225"
# rev_data = requests.get(url_review, headers=headers)
# soup_review = BeautifulSoup(rev_data.text, 'lxml')
# # print(soup_review.text)
# list_comment = get_comments(soup_review)
# for com in list_comment:
#     print("====================")
#     print(com.id)
#     print(com.date)
#     print(com.commenterName)
#     print(com.commenterLink)
#     print(com.text)