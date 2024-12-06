# Project Structure
.
├── Deliverables
│   ├── CFG.pdf
│   ├── Faults_Detected_and_Corrections.pdf
│   └── Test_Cases.pdf
├── emptyfile.txt
├── pom.xml
├── src
│   ├── main
│   │   └── java
│   │       └── com
│   │           └── begintesting
│   │               └── Printtokens.java
│   └── test
│       └── java
│           └── com
│               └── begintesting
│                   └── PrinttokensTest.java
├── target
│   ├── classes
│   │   └── com
│   │       └── begintesting
│   │           └── Printtokens.class
│   ├── site
│   │   └── jacoco
│   │       ├── com.begintesting
│   │       │   ├── Printtokens.html
│   │       │   ├── Printtokens.java.html
│   │       │   ├── index.html
│   │       │   └── index.source.html
│   │       ├── index.html
│   │       ├── jacoco-resources
│   │       │   ├── branchfc.gif
│   │       │   ├── branchnc.gif
│   │       │   ├── branchpc.gif
│   │       │   ├── bundle.gif
│   │       │   ├── class.gif
│   │       │   ├── down.gif
│   │       │   ├── greenbar.gif
│   │       │   ├── group.gif
│   │       │   ├── method.gif
│   │       │   ├── package.gif
│   │       │   ├── prettify.css
│   │       │   ├── prettify.js
│   │       │   ├── redbar.gif
│   │       │   ├── report.css
│   │       │   ├── report.gif
│   │       │   ├── session.gif
│   │       │   ├── sort.gif
│   │       │   ├── sort.js
│   │       │   ├── source.gif
│   │       │   └── up.gif
│   │       ├── jacoco-sessions.html
│   │       ├── jacoco.csv
│   │       └── jacoco.xml
│   └── test-classes
│       └── com
│           └── begintesting
│               └── PrinttokensTest.class
└── test.txt

# Requirements
Java 11 or greater
IntelliJ IDE
Maven 3.8.5
Git
**Project done on Windows 11

# Steps to Run
1. First Clone the repository to a directory on your local machine. Your project should have all of the files listed in the Project Structure above
2. Verify correct Java and Maven versions before running program
3. Run "mvn clean" to clear out any test data that may exist
4. Run "mvn test" to run the test code on the Printtokens.java file
5. Run "mvn jacoco:report" to generate the jacoco files for code coverage
6. Run "mvn site" to generate the site file that stores the coverage results from the tests ran
7. Once the site directory is generated you can view the coverage reports under target/site/jacoco/com.begintesting/index.html