# TGBA
It's the greatest bookstore around!

## How to Build
### On Linux
Execute
```
./gradlew build
```
in the project root folder.
### On Windows
Execute
```
gradlew.bat build
```
in the project root folder.

## How to Run
Make sure that your java path is correctly set. Then execute
```
java -jar ./build/libs/tgba-0.1.0.jar
```
in the project root folder.

## External Dependencies
The instructions also dictated that the implementations should have
no outside dependencies (except for well motivated 3:d party libraries).

### Included Libraries
* Apache common-lang3 version 3.5
* JUnit4 version 4.12

### Motivation
Apache common-lang3 was mostly included for the StringUtils,
EqualsBuilder and HashCodeBuilder classes.

The StringUtils class makes the code more readable by hiding
null checks and provide some String operations that are not
included in the regular String class (such as trimToEmpty).
These functions could be written by me but that would not contribute
anything to the work sample.

The EqualsBuilder class makes the implementation of an equals method
cleaner and that makes it easier to spot errors or mistakes.

JUnit4 was included to provide a unit test framework. It is perhaps the
most well known unit framework for Java. 

Unit tests are an important
part of a project. Not only to make sure that things work but also
to make refactoring safer and to in a way document the code.

## Comments
When doing the work sample the instructions was to focus on the back end
and not the UI so the UI code is a bit messy.

The interface BookList forced a design upon me that I would not have chosen
myself. The inclusion of the buy method in BookList made it impossible to
separate inventory and shopping. An inventory should in my opinion not care
about if a book is bought or if it was removed for some other reason.