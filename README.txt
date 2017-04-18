Project Title: SMS Spam Filter Android App
Created by: Sneha Dalvi

Summary:
Detects incoming SMS
Reads the content of SMS
Classifies if the SMS is legitimate or a spam by checking it’s spam features as defined below Methodology
If it is a legitimate SMS, notifies the user and saves the SMS in Inbox
Otherwise, when the app detects the SMS as a spam, it does not notify the user and silently saves the SMS in a file in application folder

Methodology: How it is decided if an sms is spam or not
First, we studied a set of spam sms and collected spam keywords.
Then, we check for spam features such as spam keywords, special characters, URL, sender number, etc.

Technology used:
Java,
Android SDK