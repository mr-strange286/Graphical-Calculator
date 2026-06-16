# Graphical Calculator

A Java Swing-based graphical calculator with graph plotting capabilities using JFreeChart and exp4j.

## Screenshot

![Calculator UI](screenshots/calculator.png)

## Features

- Basic arithmetic operations
- Function graph plotting
- Expression evaluation using exp4j
- Save graphs as PNG images
- Interactive GUI built with Java Swing

## Technologies Used

- Java
- Swing
- JFreeChart
- exp4j

## Project Structure

```
src/          Source files
lib/          External libraries
screenshots/  Images
data.csv      Data storage
```

## Compile

```bash
javac -cp "lib/*" -d bin src/*.java
```

## Run

```bash
java -cp "bin;lib/*" GraphicalCalculator
```

## Author

Rohit Rajana