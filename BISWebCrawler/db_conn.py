from bs4 import BeautifulSoup

import os
import csv
import pyodbc
import ReviewerPage
import ProductPage
from entity.user import Reviewer
from entity.book import Book

cnxn = pyodbc.connect('DRIVER={SQL Server};SERVER=10.195.25.10,54767;DATABASE=AmazonBookDB;UID=bis;PWD=bis')
cursor = cnxn.cursor()

def insert_reviewer(reviewer):
    query = "INSERT INTO Reviewer VALUES(\'" + reviewer.id + "\',\'" + reviewer.name + "\',\'" + reviewer.country + "\'," + str(reviewer.nbHelpfulVotes) + "," + str(reviewer.rank) + ",\'" + reviewer.profileText + "\',0)" #TODO
    print(query)
    cursor.execute(query)
    cnxn.commit()

def insert_book(book):
    query = "INSERT INTO Book VALUES(\'" + book.asin + "\',\'" + book.isbn10 + "\',\'" + book.isbn13 + "\',\'" + book.title + "\',\'" + book.summary + "\',\'" + book.language + "\'," + str(book.avgCustReview) + "," + str(book.nbCustReview) + "," + str(book.percFiveStar) + "," + str(book.percFourStar) + "," + str(book.percThreeStar) + "," + str(book.percTwoStar) + "," + str(book.percOneStar) + ",\'" + "0" + "\'," + str(book.rank) + ",\'" + book.publisher + "\',\'" + book.publicationDate +"\')"
    print(query)
    cursor.execute(query)
    cnxn.commit()

def insert_review(review):
    query = "INSERT INTO CustomerReview (CustomerReviewID, ASIN, NbStar, ReviewTitle, ReviewDate, ReviewerID, VerifiedPurchase, ReviewText, NbHelpful, NbVotes) VALUES(\'" + review.id + "\',\'" + review.asin + "\',\'" + str(int(review.nbStar)) + "\',\'" + review.title + "\','" + review.date + "','" + review.reviewerId + "'," + str(int(review.verifiedPurchase)) + ",'" + review.reviewText + "'," + str(review.nbHelpful) + "," + str(review.nbVotes) + ")" #TODO
    print(query)
    cursor.execute(query)
    cnxn.commit()

def check_reviewer(reviewer_id):
    cursor.execute("select * from Reviewer where ReviewerID = '" + reviewer_id + "'")
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False
