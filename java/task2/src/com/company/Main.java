package com.company;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static double findMaxEl(ArrayList<Double> arr, double maxEl, int i) {
        if(i < arr.size()) {
            double el = arr.get(i);
            if(el > maxEl)
                maxEl = el;
            return findMaxEl(arr, maxEl, i+1);
        }
        return maxEl;
    }

    static String chooseInputType(Scanner scan) {
        String type = "";
        boolean notCorrect = true;
        System.out.println("Введите [c], если хотите произвести ввод из консоли, либо [f], если хотите произвести ввод из файла.");
        while(notCorrect) {
            type = scan.nextLine();
            if(type.equals("c") || type.equals("f"))
                notCorrect = false;
            else
                System.out.println("Введите либо [c], либо [f]");
        }
        return type;
    }

    static double addElement(ArrayList<Double> sequence, String input) {
        double el = Double.parseDouble(input);
        sequence.add(el);
        return el;
    }

    static ArrayList<Double> inputSequenceFromConsole(Scanner scan) {
        ArrayList<Double> sequence = new ArrayList<>();
        boolean continueCycle = true;
        int i = 1;
        System.out.println("Введите число, либо [стоп], чтобы прекратить ввод");
        while(continueCycle) {
            System.out.println("Введите " + i + "-й элемент последовательности.");
            String input = scan.nextLine();
            if(input.equals("стоп")) {
                if(sequence.size() > 1)
                    continueCycle = false;
                else
                    System.out.println("Введите хотя бы 2 элемента последовательности.");
            } else if(input.length() > 0) {
                try {
                    double el = addElement(sequence, input);
                    if(el == Double.POSITIVE_INFINITY || el == Double.NEGATIVE_INFINITY) {
                        System.out.println("Вы ввели слишком большое число, повторите ввод.");
                        sequence.remove(i-1);
                    } else
                        i++;
                } catch (Exception err) {
                    System.out.println("Вводимый элемент должен быть вещественным числом, повторите ввод.");
                }
            } else
                System.out.println("Вы ничего не ввели, повторите ввод.");
        }
        return sequence;
    }

    static ArrayList<Double> inputSequenceFromFile(Scanner scan) {
        FileReader fr = openFile(scan);
        ArrayList<Double> sequence = new ArrayList<>();
        Scanner fileScan = new Scanner(fr);
        String inputStr = fileScan.nextLine();
        inputStr = inputStr.trim();
        String[] nums = inputStr.split("\\s+");
        for(String num : nums)
            addElement(sequence, num);
        return sequence;
    }

    static boolean isFileCorrect(FileReader fr) {
        Scanner frScan = new Scanner(fr);
        boolean result = true;
        try {
            String inputStr = frScan.nextLine();
            String[] nums = inputStr.split("\\s+");
            for(String num : nums)
                Double.parseDouble(num);
        } catch (Exception err) {
            result = false;
        }
        return result;
    }

    static FileReader openFile(Scanner scan) {
        String filePath;
        boolean notCorrect = true;
        FileReader fr = null;
        while(notCorrect) {
            System.out.println("Введите путь к файлу");
            filePath = scan.nextLine();
            try {
                fr = new FileReader(filePath);
                if(isFileCorrect(fr))
                    notCorrect = false;
                else
                    System.out.println("В файле должны присутствовать только вещественные числа, разделенные пробелом и записанные в одну строку. Измените файл и повторите ввод.");
                fr.close();
            } catch (IOException err) {
                System.out.println("Произошла ошибка при чтении файла. Убедитесь, что такой файл существует и повторите ввод.");
                notCorrect = true;
            }
        }
        return fr;
    }

    static ArrayList<Double> inputSequence(Scanner scan, String inputType) {
        if(inputType.equals("c"))
            return inputSequenceFromConsole(scan);
        else
            return inputSequenceFromFile(scan);
    }

    public static void main(String[] args) {
        System.out.println("Данная программа находит максимальный элемент введенной последовательности.");
        Scanner scan = new Scanner(System.in);
        String inputType = chooseInputType(scan);
        ArrayList<Double> sequence = inputSequence(scan, inputType);
        double maxEl = Math.round(findMaxEl(sequence, sequence.get(0), 0));
        System.out.println("Максимальный элемент последовательности = " + maxEl);
    }
}
