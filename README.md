# SMS Messenger Application

A simple, clean desktop application built with Java Swing that allows you to send SMS messages using the Twilio API.

## Features
- User-friendly interface
- Real-time SMS sending
- Status notifications
- Background processing for API calls
- Error handling

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- An active Twilio account
- All required dependencies (see below)

## Required Dependencies
The following JAR files must be added to your project's classpath:

1. Twilio SDK:
  - twilio-java-9.14.1.jar

2. Jackson Libraries:
  - jackson-core-2.15.2.jar
  - jackson-databind-2.15.2.jar
  - jackson-annotations-2.15.2.jar

3. Apache HTTP Components:
  - httpclient-4.5.14.jar
  - httpcore-4.4.16.jar
  - commons-logging-1.2.jar
  - commons-codec-1.11.jar

4. Google Guava:
  - guava-32.1.3-jre.jar

5. Joda Time:
  - joda-time-2.12.5.jar

## Setup

1. Sign up for Twilio:
  - Go to https://www.twilio.com/try-twilio
  - Create a free account
  - Get your Account SID and Auth Token
  - Get a Twilio phone number

2. Configure the application:
  - Replace ACCOUNT_SID with your Twilio Account SID
  - Replace AUTH_TOKEN with your Twilio Auth Token
  - Replace FROM_NUMBER with your Twilio phone number

3. Install Dependencies:
  - Add all required JAR files to your project's classpath
  - If using Maven, add the corresponding dependencies to your pom.xml

## Usage

1. Enter the recipient's phone number in international format (e.g., +1234567890)
2. Type your message in the text area
3. Click "Send SMS"
4. Watch the status label for sending confirmation

## Error Handling
The application handles various errors including:
- Empty fields
- Invalid phone numbers
- Network issues
- API errors

## Notes
- Phone numbers must be in international format (+1234567890)
- Messages are sent asynchronously to keep the UI responsive
- The message area clears automatically after successful sending
- Status updates are shown at the bottom of the window

## Troubleshooting

Common issues and solutions:

1. Missing Dependencies:
  - Ensure all required JAR files are in your classpath
  - Check for version conflicts

2. Authentication Errors:
  - Verify your Twilio credentials are correct
  - Check that your Twilio account is active

3. Sending Failures:
  - Verify the recipient's phone number format
  - Check your internet connection
  - Ensure your Twilio account has sufficient credit

## License
This project is open source and available under the MIT License.

## Support
For issues with:
- Twilio API: Visit https://www.twilio.com/support
- Application bugs: Open an issue in the repository
- Dependencies: Check the respective library documentation

## Acknowledgments
- Twilio for providing the SMS API
- Substance Look and Feel for the UI theme