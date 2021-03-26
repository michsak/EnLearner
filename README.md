# EnLearner
Wear OS app, created in Android Studio, tested on Fossil Gen 5.
The app purpose is to learn english words, which are stored in the AWS database.


## Downloading content
As mentioned before words are downloaded from AWS (Amazon Elastic Compute Cloud - EC2). <br>
The whole process is taking place in the background - it starts when app is launching and if there's no connection to the internet at the moment when the connection is established.<br>
In order to prevent user from the situation when he or she doesn't have access to the Internet at the moment, on every application start, when there's established an internet 
connection, there are downloaded 10 words.

## Web-scraping
User click on the displayed word starts a web-scraping action. <br>
Firstly there was a need to create a first word from Word document(docx/doc) scraper ([available here][1]).
Secondly there was created a Cambridge Dictionary web-scraper ([available here][2]) and afterwards it was implemented to Java project. <br>
Web-scraper is scraping first definition of given words and first usage example of this word.

## Notification
When the user first opens the app he or she can choose the interval of emerging new words. <br>
Notifications work also in the background, when the user is not currently using the app. <br>
They repeatedly remind the user to open the app and check out the new word.


## Overview
<img src="https://github.com/michsak/EnLearner/blob/master/overview/gif-overview.gif" width="350" height="350" />

<br>
The app is available on the Goole Play store, so do not hesitate and give it a try.

[1]: https://github.com/michsak/EnLearner/tree/master/first_word_scraper
[2]: https://github.com/michsak/EnLearner/tree/master/web_scraper
