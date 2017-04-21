from bs4 import BeautifulSoup
import requests
import time
from entity.book import Book
from entity.book import Review
from entity.book import Comment
from entity.user import Author
import re
from datetime import datetime
from urllib import parse
import ReviewPage
import AuthorPage
import json

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246',
    'Connection': 'keep-alive',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
    # 'Cookie': 'aws-target-static-id=1489245108811-141740; aws-session-id=156-4065030-0477716; aws-session-id-time=2119965116l; __utmz=194891197.1489245117.1.1.utmccn=(referral)|utmcsr=portal.aws.amazon.com|utmcct=/billing/signup|utmcmd=referral; aws-business-metrics-last-visit=1489245154635; at-main=Atza|IwEBIHSbBpLorVVuewZnDIZ0aHAk7gCjO2MNrL_J0eKo1qjt5DImSZ1_e_SJzVA_IJUDqji7LqPj2QtmhVOOui63_MB1hXEvhCBo_NXJQJo2bjeqNb1AahoT_NHMoYKjcBegQtVk3df93HT6erZC_2MQ-yzCm0ILv4FSG2qwV2Ph-OQu_HxmBrjE_DiQWI2RQbsX_FGoO0dbcAhN4mflXKDm8AnH802H69kesHfnObykuAoDEUA-Xp8wrYfoe6DPVF6K-UAWg57VNa0z6co0SpM-hdDaNgmZmYKmms7lfjI0YR2WvZM38F_kMZJblOUDcFiQxVXr0Zw2nFb2W9WtN03I20vpbOz7t28VcE90lMRByJn4KrkVLN6lIcFttmI-KxsrRn7bzxlC6TSFT7mAt-B2VoGT; s_pers=%20s_nr%3D1489246399697-New%7C1497022399697%3B; aws-ubid-main=167-8061496-9307626; aws-session-token="B9IKQJ8DgUo7/RD2wFnPePkChgBfKfp88Wxi/aDugtCQs5uTOlAOyqi+LZwLd5dUtnEDP4/UQ7z1o8rmZeSUmTfZVP4Mmd4dYi0sCWkE8zcK1WCQrmBN/La/ksYjiKiuHpZVvMzLmCMYdvc6Tk30TKTlUAs037A5lVrzNDRI+70WNZTY3NQ6lsNvm8cnXSeFbEQ8igrlXtTSsnQ2tcMHUl4Tlmcsp9oWvv+LM1/WHig="; aws-x-main="3BFjf4BvmfbKgpC@K9kqtJymppleuwmKu8VShIWyYpTAtxDHO7weD@zO7k6LqI?7"; aws-at-main=Atza|IwEBIFsxs2wPNih_InYAqlUSIciX6jd1SZi9OixDqjB-SFIQb8ZJY10bUp7fDfPFKoUSAhObKoP6Y_5fB_EZvtaunqx2AQdEUXfpZo0Pzdsb2YhC6rTC2mxqP4qMerJmXIQOKX3r7xCSMHluM6QuRUwV11RsjzSLWKjxe_Ge_5oFbFroqtPE30xihw5lQ80OhSIXmU7UPVaaPstEw2MQVMtYs7yBB-dlvCSCqoYQ4PcxBKlE_WaGJFx3ZhaYbnhqZoYzgkXd3lseiaJeAPHbVweuToaaODZOeECu2ntopN1ha6IJerGA-xOo5ViU9-0Hwwwgz--t7GGxd3zYXe8TTNssSvjbU64KAQU43ZFLxhn3rhizuS6KA93puWqms7uLo6PIXPmosy96w4k-fZIF3z8TmpzCEGrL_-Cwn55uG8EtuUbzWg; aws-userInfo=%7B%22arn%22%3A%22arn%3Aaws%3Aiam%3A%3A239407844960%3Aroot%22%2C%22alias%22%3A%22%22%2C%22username%22%3A%22Martina%2520Megasari%22%2C%22keybase%22%3A%22t%2FiDMMp5mH2FCW%2FyDfZPVxwchhtfw72RhtpTSPoJIZA%5Cu003d%22%2C%22issuer%22%3A%22https%3A%2F%2Fwww.amazon.com%2Fap%2Fsignin%22%7D; __utmv=194891197.%223BFjf4BvmfbKgpC%40K9kqtJymppleuwmKu8VShIWyYpTAtxDHO7weD%40zO7k6LqI%3F7%22; __utma=194891197.1182645527.1489245117.1489245117.1489247733.2; x-wl-uid=1DjBuj9VVmpXjGXmdVkEVcHODuJdefI1T6L1UYP0qk9ui9jMqvuhPBZYvelUO0eCOJm7EasnyE1iwf994H9TTjui//aV3zczmiFb9d6bnm8xySZorrwLxO6+uc/IeB/2fcdUkc/JOpTs=; aws-target-visitor-id=1489245108821-964344.26_9; aws-target-data=%7B%22support%22%3A%221%22%7D; s_fid=36C1E738A6DD20A0-302516AC101FAA34; s_vn=1520780379735%26vn%3D3; regStatus=registered; x-main="FNIGhPmbd0oaxCBt6X6pgyCZNN?Fq6AbWBNeE96SR90SM1y5eLGly05t3Vv5w6VE"; lc-main=en_US; UserPref=XXUPcxa+FV+O7fDvfvVUk3uLGYKO86S924fjnBybPlUXwKJuRPi+Dk0QzrRdHj2HZRo+vp0j/s3coZaepgCwUs9z8XIK7NyUXzL/USVhYrRBZxbKGYfBinDQ45Yo8zrV65O6hhCYUtTKjEU16oizGH8ekTlXxfIA17xrB+ajg7nt9O2BWpwc1kN1JXGJQVHmG0zYHAr5yBnADKxNhzOJnii2q+s+CLudTTyAMp+aCW3gtfa1Fpb5x58EVv3sgqunUqPbhdleFbLDkssd31OMlQ9ex04sO0ISsqPmDLY4S2AGSGev4VpPgoL7yTNd1ePtaLbgZx9pMPEJQ9YILn5QAWpjTM5y46hN4PFiG6EZ3MJk8I3NKs3AJVsEeCBpI3v7QMnMRrlI4Yhc7PWIE2dShcg5vjaWjA3Zo1Htugxj71gcUJRysR01b4ChVQkfaNrm; s_vnum=1922455017278%26vn%3D2; s_cc=true; s_nr=1490898077684-Repeat; s_dslv=1490898077687; s_sq=%5B%5BB%5D%5D; s_ppv=63; session-token="MCnZ7mg+vJHITDmnIEQ+5YgjVXOa/ziJ/RfaPXE13HfZlWpJu9LXslKZ099AsKQvO9iAh42xRKq7twXk939hwB8KSwSNw4VP1nRPGmihzMqymXpO2p2HeSBzuOQuL3OAZI7sk8seog7obynJJkCZD8mB/ShoZxr3DvhHQTXcZQHBaaQVL0vNQhYUpLWCszjN6MkHAlp/stsH012cKzgTqun3q01/YJqqTaqr9j3wvmUlv0jh3YAZ3ni6sSG43T4WL7wFxJPQRKnGDyomK7C3/Q=="; ubid-main=154-3030463-1341322; session-id-time=2082787201l; session-id=152-8165811-0496857; csm-hit=M9G564RTVHW5NNVVZEWW+s-M9G564RTVHW5NNVVZEWW|1490941393593'
    # 'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' # don't remove!
}

def get_book(asin, url, urlReview):
    wb_data = requests.get(url, headers=headers)

    # time.sleep(1)
    soup = BeautifulSoup(wb_data.text,'lxml')
    # print(soup.prettify())
    # parse book data: title
    booktitles=soup.select('div > div.a-fixed-left-grid-col.a-col-right > div > div > div.a-fixed-left-grid-col.product-info.a-col-right > div.a-row.product-title > h1 > a')
    # print(booktitles.prettify())
    booktitle = None
    booktitle = soup.find(id = "productTitle")
    if booktitle is None:
        booktitle = soup.find(id = "ebooksProductTitle")
    title = booktitle.get_text()
    book = Book(asin, '', '', title)
    # end parse book data: title

    # parse book data: star
    tbl_review = soup.find(id = "histogramTable")
    perc5Star = 0
    perc4Star = 0
    perc3Star = 0
    perc2Star = 0
    perc1Star = 0

    el5star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 5star histogram-review-count']})
    el4star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 4star histogram-review-count']})
    el3star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 3tar histogram-review-count']})
    el2star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 2star histogram-review-count']})
    el1star = tbl_review.find(attrs={'class': ['a-size-base a-link-normal 1star histogram-review-count']})

    if el5star is not None :
        perc5Star = el5star.get_text().replace('%','')
    if el4star is not None :
        perc4Star = el4star.get_text().replace('%','')
    if el3star is not None :
        perc3Star = el3star.get_text().replace('%','')
    if el2star is not None :
        perc2Star = el2star.get_text().replace('%','')
    if el1star is not None :
        perc1Star = el1star.get_text().replace('%','')

    book.set_star_percentage(perc5Star, perc4Star, perc3Star, perc2Star, perc1Star)
    # end parse book data: star

    # parse book data : category
    category = soup.find(id="wayfinding-breadcrumbs_feature_div").select('a')
    category_text = None;
    separator = ">"
    # print
    for x in range(1,len(category)):

        txt = category[x].get_text().strip()

        if category_text == None:
            category_text = txt
        else:
            category_text = separator.join([category_text, txt])
    book.category = category_text
    # end parse book data : category

    # parse book data : summary
    idx_start_summary = wb_data.text.find("bookDescEncodedData = \"")
    summary_temp_text = wb_data.text[idx_start_summary + 23:]
    idx_end_summary = summary_temp_text.find("\"")
    summary_text = summary_temp_text[:idx_end_summary]
    summary_text = parse.unquote(summary_text)
    book.summary = summary_text
    # end parse book data : summary

    # parse book detail
    language = ''
    publisher = ''
    rank = ''
    publication_date = ''
    isbn10 = ''
    isbn13 = ''

    # parse book data : language, publisher, publication date, isbn-10, isbn-13, rank
    div_product_detail = soup.find(id = "productDetailsTable").find(attrs = {'class': 'content'})
    list_detail = div_product_detail.select('li')

    for ls in list_detail:
        ls = str(ls)
        if "<li><b>Language:</b>" in ls:
            language = ls.replace("<li><b>Language:</b> ", "").replace("</li>","")
        elif "<li><b>Publisher:</b>" in ls:
            publisher = ls.replace("<li><b>Publisher:</b> ", "").replace("</li>", "")
        elif "<li><b>Publication Date:</b>" in ls:
            publication_date = ls.replace("<li><b>Publication Date:</b> ", "").replace("</li>", "")
        elif "<li><b>ISBN-10:</b>" in ls:
            isbn10 = ls.replace("<li><b>ISBN-10:</b> ", "").replace("</li>", "")
        elif "<li><b>ISBN-13:</b>" in ls:
            isbn13 = ls.replace("<li><b>ISBN-13:</b> ", "").replace("</li>", "")
        elif "<b>Amazon Best Sellers Rank:</b>" in ls:
            indexStart = ls.find('#')
            indexEnd = ls.find(' ',indexStart)
            rank = ls[indexStart+1:indexEnd].replace(",","")
            if not rank.isdigit():
                rank = 0
            # print("Rank : ", rank)

    book.publisher = publisher
    book.language = language
    book.publicationDate = publication_date
    book.isbn10 = isbn10
    book.isbn13 = isbn13
    book.rank = rank
    # end parse book data : language, publisher, publication date, isbn-10, isbn-13, rank

    # parse book data : avgcustreview
    rating = soup.find('span', attrs={'class', 'arp-rating-out-of-text'}).get_text()
    rating = float(rating.partition(' ')[0])
    book.avgCustReview = rating
    # end parse book data : avgcustreview

    # parse book data : authors
    authors = soup.select('span.author')
    list_author = []
    for a in authors:
        # print(a.prettify())
        author_id = None
        asin_container = a.find('a', class_= "contributorNameID")
        # print(asin_container.prettify())
        a = a.find('a',class_="a-link-normal")
        author_href = a.get('href')
        author_name = a.text
        is_registered = 0;
        # print(a.prettify())

        if asin_container is not None:
            author_id = asin_container.get('data-asin')
            if author_id is not None:
                is_registered = 1
        else:
            author_id = author_name.replace(" ", "")

        author = Author(author_id)
        print(author_id)
        author.name = author_name
        author.link = "https://www.amazon.com" + author_href
        author.is_registered = is_registered
        print("Reg " , is_registered)
        if is_registered == 1:
            add_info = AuthorPage.get_author(author.link)
            author.biography = add_info['biography']
            author.rank = add_info['rank']

        list_author.append(author)
    book.listAuthor = list_author
    # print(list_author)
    # parse book data : authors

    # parse book frequently bought together
    listFBT = [];
    fbtSoup = soup.find(id='sims-fbt-content')
    # fbt_real = fbt.find(id='dp')
    if fbtSoup is not None :
        form_fbt = fbtSoup.find_all('form')

        inputs = form_fbt[0].find_all("input", type="hidden")

        for f in inputs:
            if f.get('name') != "session-id":
                listFBT.append(f.get('value'))
    # print(listFBT)
    book.listFBT = listFBT
    # end parse book frequently bought together

    # parse book also bought
    listAB = [];
    abSoup = soup.find(id='purchase-sims-feature')
    if abSoup is not None:
        jsonDiv = abSoup.find_all('div')
        abJson = json.loads(jsonDiv[0]._attr_value_as_string('data-a-carousel-options'))
        listAB = abJson['ajax']['id_list']
    book.listAB = listAB
    # end parse book also bought

    # get review data
    pageNb = 0;
    print(urlReview)
    urlTemp = urlReview + "/ref=cm_cr_getr_d_show_all?pageNumber=" + str(pageNb) + "&pageSize=50&reviewerType=all_reviews"  # max page size = 50
    wb_data_review = requests.get(urlTemp, headers=headers)

    # time.sleep(1)
    soupReview = BeautifulSoup(wb_data_review.text, 'lxml')

    # parse book data: review
    list_book_review = get_review(soupReview,asin)
    # print(soupReview.prettify())
    paging = soupReview.select('ul.a-pagination > li.a-last > a')
    print(len(paging))

    while len(paging) > 0: #TODO
        time.sleep(2)
        pageNb += 1
        urlTemp = urlReview + "/ref=cm_cr_getr_d_show_all?pageNumber=" + str(pageNb) + "&pageSize=50&reviewerType=all_reviews"
        # print(str(pageNb) + ":" + urlTemp)
        wb_data = requests.get(urlTemp, headers=headers)
        soup = BeautifulSoup(wb_data.text, 'lxml')
        list_book_review.extend(get_review(soup,asin))
        paging = soup.select('ul.a-pagination > li.a-last > a')
        # print(len(list_book_review))
    book.listCustReview = list_book_review;
    book.nbCustReview = len(list_book_review)
    # end parse book data: review

    # data_raw = body[0].find('div')._attr_value_as_string('data');
    # data = json.loads(data_raw)
    return book


def get_review(soup,asin):
    list_review = []
    ASIN = asin
    reviews = soup.select('div[data-hook="review"]')
    for r in reviews:

        review = Review(ASIN)

        #id
        id = r.get('id')
        review.id = id

        # title
        titleTag = r.find('a', attrs={'data-hook': 'review-title'})
        title = titleTag.get_text()
        review.link = titleTag.get('href')
        review.title = title.strip().replace('\'', '\'\'')

        # star
        star = r.find('i', attrs = {'data-hook':'review-star-rating'}).get_text();
        review.nbStar = float(star.partition(' ')[0])

        # author and author link
        dom_author = r.find('a', attrs = {'data-hook':'review-author'})
        author_link = dom_author.get('href')
        author = dom_author.get_text();
        review.reviewerId = author_link.split('/')[4]
        review.reviewerLink = "https://www.amazon.com" + author_link
        review.reviewerName = author

        # is verified
        is_verified =  r.find('span', attrs = {'data-hook':'avp-badge'})
        if is_verified is None:
            is_verified = False
        else:
            is_verified = True
        review.verifiedPurchase = is_verified

        # review text
        review.reviewText = r.find('span', attrs = {'data-hook':'review-body'}).get_text().strip().replace('\'', '\'\'')

        # review date
        date_text = r.find('span', attrs = {'data-hook':'review-date'}).get_text()
        date_text = date_text.replace('on ', '')
        date = datetime.strptime(date_text, '%B %d, %Y')
        review.date = date.strftime('%Y-%m-%d')

        # helpful and votes

        vote_text = r.find('span', attrs={'data-hook': 'review-voting-widget'}).get_text()
        indexOf = vote_text.find('of')

        nbHelpful = vote_text.strip().partition(' ')[0]
        nbVotes = nbHelpful
        if indexOf > 0:
            nbVotes = int(vote_text[indexOf+3:].strip().partition(' ')[0])

        if nbHelpful == "One":
            nbHelpful = 1
        elif not nbHelpful.isdigit():
            nbHelpful = 0
        else:
            nbHelpful = int(nbHelpful)

        if nbVotes == "One":
            nbVotes = 1
        elif not nbVotes.isdigit():
            nbVotes = 0
        else:
            nbVotes = int(nbVotes)

        review.nbHelpful = nbHelpful
        review.nbVotes = nbVotes

        # comment
        comment_text = r.find("span", class_ = "review-comment-total").text
        nbComments = int(r.find("span", class_ = "review-comment-total").text)
        review.nbComments = nbComments

        list_comment = []
        if nbComments > 0:
            url_review = "https://www.amazon.com" + review.link
            rev_data = requests.get(url_review, headers=headers)
            soup_review = BeautifulSoup(rev_data.text, 'lxml')
            list_comment = ReviewPage.get_comments(soup_review)
            for com in list_comment:
                com.reviewId = review.id

        review.listComment = list_comment

        list_review.append(review)

    return list_review

# asin = '0544227751'
# url = "https://www.amazon.com/Big-Data-Revolution-Transform-Think/dp/0544227751"
# url_review = "https://www.amazon.com/Big-Data-Revolution-Transform-Think/product-reviews/0544227751"
# asin = 'B06XQCKG2Z'
# url = "https://www.amazon.com/Becoming-Robin-Tale-Self-Discovery-ebook/dp/B06XQCKG2Z/ref=zg_bs_10368555011_49/141-6728850-2537519?_encoding=UTF8&psc=1&refRID=4KQ2WHHYM5PPPVMEJJPP"
# url_review = "https://www.amazon.com/product-reviews/B06XQCKG2Z"
# get_book(asin, url,url_review);

# asin = '059035342X'
# url = "https://www.amazon.com/Harry-Potter-Sorcerers-Stone-Rowling/dp/059035342X/ref=zg_bs_10368555011_3?_encoding=UTF8&psc=1&refRID=F5CM5FW3AZDS0D5H5WKV"
# url_review = "https://www.amazon.com/product-reviews/059035342X"
# get_book(asin, url,url_review);