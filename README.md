# C_Compiler
Welcome to the C Compiler Project. This compiler is developed in three main components: **Scanner**, **Parser**, and **Code Generator**. Below you will find information about each part, including the current status and planned release dates for the in-progress components.

## Table of Contents

- [Overview](#overview)
- [Components](#components)
   - [Scanner](#scanner)
   - [Parser](#parser)
   - [Code Generator](#code-generator)
- [Installation](#installation)
- [Usage](#usage)

## Overview 

This project aims to create a C compiler written in Java, structured into three distinct components. **Scanner** is fully implemented and operational, while **Parser** and **Code Generator** are under development. The project is expected to be completed by 12/12/24.

## Components

### Scanner

- **Status**: Completed
- **Description**: The scanner defines a state-transition table in the form of a two-dimensional array, receives input in the form of minimal dialect syntax from a file, and outputs the corresponding tokens of the file.

### Parser

- **Status**: In Progress.
- **Expected Release**: 10/25/24

### Code Generator

- **Status**: In Progress.
- **Expected Release**: 11/15/24

## Installation

To get started, clone the repository and follow the installation instructions:

```bash
git clone https://github.com/lukehawranick/C_Compiler.git
cd C_Compiler
```

## Usage

The Scanner can interpret components of the C language laid out by the minimal dialect. Write a C program abiding by the minimal dialect and name the file 'sourcecode.myc'. Compile and run the project with

```
javac Compiler.java
java Compiler.java
```

