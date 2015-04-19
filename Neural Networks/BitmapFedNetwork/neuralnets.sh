#!/bin/bash
cd DIR # GET TO LOCATION OF CODE
javac *.java
echo "Running ATCS: Neural Networks Code"
echo "Train or Test?"
select yn in "Train" "Test" "Exit"; do
    case $yn in
        Train ) java -Xmx6g Perceptron train_config.txt; exit;;
        Test ) java -Xmx6g Perceptron test_config.txt; exit;;
        Exit ) echo "program terminated"; exit;;
    esac
done