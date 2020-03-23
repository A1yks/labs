package com.company;

import java.io.*;
import java.util.*;

class Employee implements Serializable {
    String surname;
    String department;
    double salary;

    Employee(String surname, String department, double salary) {
        this.surname = surname;
        this.department = department;
        this.salary = salary;
    }
}

class DepartmentData {
    String name;
    double sum = 0;

    DepartmentData(String name, double sum) {
        this.name = name;
        this.sum = sum;
    }
}

public class Main {
    static void showInfo() {
        System.out.println("Выберите, что вы хотите сделать (введите в консоль значение из квадратных скобок):");
        System.out.println("| Добавить сотрудников [доб] | Изменить данные сотрудников [изм] | Удалить сотрудников [уд] | Вывести информацию о сотрудниках на экран [выв] | Вывести сумму выплат по отделам за месяц [сум] | Завершить работу программы [выход] |");
    }

    static String chooseAction(Scanner scan) {
        ArrayList<String> actions = new ArrayList(Arrays.asList("доб", "изм", "уд", "выв", "сум", "выход"));
        String choice = scan.nextLine();
        while (actions.indexOf(choice) == -1) {
            System.out.println("Вы должны выбрать существующее действие.");
            choice = scan.nextLine();
        }
        return choice;
    }

    static String makeChoice(ArrayList<String> choices, Scanner scan) {
        String choice = "";
        while (choices.indexOf(choice) == -1) {
            System.out.println("Введите либо [д], либо [н]");
            choice = scan.nextLine();
        }
        return choice;
    }

    static int changeEmployeeData(Employee emp, Scanner scan, int changes) {
        String choice = "";
        ArrayList<String> choices = new ArrayList(Arrays.asList("д", "н"));
        boolean notCorrect = true;
        while (notCorrect) {
            System.out.println("Хотите изменить фамилию?");
            choice = makeChoice(choices, scan);
            if (choice.equals("д")) {
                System.out.println("Введите новую фамилию");
                String surname = scan.nextLine();
                if (surname.length() > 0) {
                    emp.surname = surname;
                    System.out.println("Фамилия успешно изменена, хотите продолжить изменение данных?");
                    notCorrect = false;
                    changes++;
                    choice = makeChoice(choices, scan);
                    if (choice.equals("д"))
                        changeEmployeeData(emp, scan, changes);
                } else
                    System.out.println("Фамилия не может быть пустой, повторите ввод.");
            } else {
                System.out.println("Хотите изменить наименование отдела?");
                choice = makeChoice(choices, scan);
                if (choice.equals("д")) {
                    System.out.println("Введите наименование отдела");
                    String department = scan.nextLine();
                    if (department.length() > 0) {
                        emp.department = department;
                        System.out.println("Наименование отдела успешно изменено, хотите продолжить изменение данных?");
                        notCorrect = false;
                        changes++;
                        choice = makeChoice(choices, scan);
                        if (choice.equals("д"))
                            changeEmployeeData(emp, scan, changes);
                    } else
                        System.out.println("Название отдела не может быть пустым, повторите ввод.");
                } else {
                    System.out.println("Хотите изменить размер заработной платы?");
                    choice = makeChoice(choices, scan);
                    if (choice.equals("д")) {
                        System.out.println("Введите размер заработной платы");
                        try {
                            double salary = Double.parseDouble(scan.nextLine());
                            if (salary > -1) {
                                notCorrect = false;
                                emp.salary = salary;
                                System.out.println("Заработная плата успешно изменена");
                                changes++;
                            } else {
                                notCorrect = true;
                                System.out.println("Заработная плата не может быть отрицательной, повторите ввод");
                            }
                        } catch (Exception err) {
                            System.out.println("Заработная плата должна быть числом");
                        }
                    }
                }
            }
        }
        return changes;
    }

    static void addData(Scanner scan) {
        ArrayList<Employee> data = new ArrayList<Employee>();
        boolean continueCycle = true;
        boolean notCorrect = true;
        System.out.println("Если хотите прекратить ввод, введите [стоп] вместо фамилии сотрудника");
        while (continueCycle) {
            String surname = "";
            String department = "";
            double salary = 0;
            while (notCorrect) {
                System.out.println("Введите фамилию сотрудника");
                surname = scan.nextLine();
                if(surname.length() > 0) {
                    if (surname.equals("стоп")) {
                        continueCycle = false;
                        notCorrect = false;
                    } else {
                        System.out.println("Введите наименование отдела");
                        department = scan.nextLine();
                        if(department.length() > 0) {
                            System.out.println("Введите заработную плату сотрудника");
                            try {
                                salary = Double.parseDouble(scan.nextLine());
                                if (salary > -1)
                                    notCorrect = false;
                                else {
                                    notCorrect = true;
                                    System.out.println("Заработная плата не может быть отрицательной, повторите ввод");
                                }
                            } catch (Exception err) {
                                notCorrect = true;
                                System.out.println("Возникла ошибка при вводе данных, повторите ввод.");
                            }
                        } else
                            System.out.println("Название отдела не может быть пустым, повторите ввод.");
                    }
                } else
                    System.out.println("Фамилия не может быть пустой, повторите ввод.");
            }
            if (!surname.equals("стоп")) {
                notCorrect = true;
                data.add(new Employee(surname, department, salary));
            }
        }
        saveDataToFile(data, scan, true);
        showInfo();
        executeAction(chooseAction(scan), scan);
    }

    static void createNewFile(ArrayList<Employee> data, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeInt(data.size());
            for (Employee emp : data)
                oos.writeObject(emp);
            System.out.println("Данные успешно сохранены в файл " + filePath);
        } catch (Exception err) {
            System.out.println("Не удалось сохранить данные в файл.");
        }
    }

    static void updateFile(ArrayList<Employee> newData, Scanner scan, String filePath) {
        ArrayList<Employee> data = grabData(scan, false, filePath);
        data.addAll(newData);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeInt(data.size());
            for (Employee emp : data)
                oos.writeObject(emp);
            System.out.println("Данные успешно обновлены в файле " + filePath);
        } catch (Exception err) {
            System.out.println("Не удалось обновить данные в файле.");
        }
    }

    static void saveDataToFile(ArrayList<Employee> data, Scanner scan, boolean update) {
        System.out.println("Введите [д], если хотите сохранить данные в файл.");
        String choice = scan.nextLine();
        if (choice.equals("д")) {
            System.out.println("Введите имя файла");
            String filePath = scan.nextLine() + ".dat";
            File file = new File(filePath);
            if (update && file.exists()) {
                System.out.println("Файл с таким именем уже существует. Хотите его обновить? [д/н] (При выборе [н] создастся новый файл)");
                choice = scan.nextLine();
                if (choice.equals("д"))
                    updateFile(data, scan, filePath);
                else
                    createNewFile(data, filePath);
            } else
                createNewFile(data, filePath);
        }
    }

    static void changeData(Scanner scan) {
        ArrayList<Employee> data = grabData(scan, true, null);
        if (data.size() > 0) {
            boolean continueCycle = true;
            boolean notCorrect = true;
            int changedCount = 0;
            while (continueCycle) {
                System.out.println("Введите номер сотрудника, данные которого хотите изменить, либо [стоп], чтобы вернуться в меню");
                while (notCorrect) {
                    String input = scan.nextLine();
                    String choice = "";
                    if (input.equals("стоп")) {
                        notCorrect = false;
                        continueCycle = false;
                    } else {
                        try {
                            int changeNum = Integer.parseInt(input);
                            Employee emp = data.get(changeNum - 1);
                            changedCount = changeEmployeeData(emp, scan, 0);
                            notCorrect = false;
                        } catch (Exception err) {
                            System.out.println("Сотрудник с таким номером не найден, повторите ввод.");
                        }
                    }
                }
                notCorrect = true;
            }
            if (changedCount > 0)
                saveDataToFile(data, scan, false);
        }
        showInfo();
        executeAction(chooseAction(scan), scan);
    }

    static void deleteData(Scanner scan) {
        ArrayList<Employee> data = grabData(scan, true, null);
        if (data.size() > 0) {
            boolean continueCycle = true;
            boolean notCorrect = true;
            int deletedCount = 0;
            while (continueCycle) {
                System.out.println("Введите номер сотрудника, которого хотите удалить, либо [стоп], чтобы вернуться в меню");
                while (notCorrect) {
                    String input = scan.nextLine();
                    if (input.equals("стоп")) {
                        notCorrect = false;
                        continueCycle = false;
                    } else {
                        try {
                            int deleteNum = Integer.parseInt(input);
                            data.remove(deleteNum - 1);
                            deletedCount++;
                            System.out.println("Сотрудник № " + deleteNum + " успешно удален.");
                            notCorrect = false;
                        } catch (Exception err) {
                            System.out.println("Сотрудник с таким номером не найден, повторите ввод.");
                            notCorrect = true;
                        }
                    }
                }
                notCorrect = true;
            }
            if (deletedCount > 0)
                saveDataToFile(data, scan, false);
        }
        showInfo();
        executeAction(chooseAction(scan), scan);
    }

    static void showData(Scanner scan) {
        grabData(scan, true, null);
        showInfo();
        executeAction(chooseAction(scan), scan);
    }

    static ArrayList<Employee> grabData(Scanner scan, boolean show, String filePath) {
        if (filePath == null) {
            System.out.println("Введите имя файла");
            filePath = scan.nextLine() + ".dat";
        }
        ArrayList<Employee> data = new ArrayList<Employee>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            int size = ois.readInt();
            for (int i = 0; i < size; i++) {
                Employee emp = (Employee) ois.readObject();
                data.add(emp);
            }
            if (show) {
                if(data.size() > 0) {
                    System.out.println("Данные, записанные в файле:");
                    showArr(data);
                } else
                    System.out.println("В файле не найдено ни одной записи");
            }
        } catch (Exception err) {
            System.out.println("Произошла ошибка при чтении файла. Убедитесь, что такой файл существует.");
        }
        return data;
    }

    static ArrayList<DepartmentData> grabDepartments(ArrayList<Employee> data) {
        ArrayList<DepartmentData> departments = new ArrayList<DepartmentData>();
        for (Employee emp : data) {
            DepartmentData department = grabCurrentDepartment(emp.department, departments);
            if (department == null)
                departments.add(new DepartmentData(emp.department, emp.salary));
            else
                department.sum += emp.salary;
        }
        return departments;
    }

    static DepartmentData grabCurrentDepartment(String departmentName, ArrayList<DepartmentData> departments) {
        boolean isInArr = false;
        int i = 0;
        DepartmentData department = null;
        while (!isInArr && i < departments.size()) {
            DepartmentData temp = departments.get(i);
            if (temp.name.equals(departmentName)) {
                isInArr = true;
                department = temp;
            }
            i++;
        }
        return department;
    }

    static void sortDepartments(ArrayList<DepartmentData> departments) {
        for (int i = 0; i < departments.size(); i++) {
            for (int j = 1; j < departments.size() - i; j++) {
                DepartmentData prev = departments.get(j - 1);
                DepartmentData curr = departments.get(j);
                if (curr.name.compareToIgnoreCase(prev.name) < 0) {
                    departments.set(j, prev);
                    departments.set(j - 1, curr);
                }
            }
        }
    }

    static void showDepartmentSum(Scanner scan) {
        ArrayList<Employee> data = grabData(scan, false, null);
        ArrayList<DepartmentData> departments = grabDepartments(data);
        sortDepartments(departments);
        for (DepartmentData department : departments) {
            System.out.println(department.name);
            System.out.println("Общая сумма выплат за месяц по отделу = " + department.sum + "\n");
        }
        showInfo();
        executeAction(chooseAction(scan), scan);
    }

    static void showArr(ArrayList<Employee> data) {
        for (int i = 0; i < data.size(); i++) {
            Employee emp = data.get(i);
            System.out.println("Сотрудник №" + (i + 1));
            System.out.println("Фамилия: " + emp.surname);
            System.out.println("Наименование отдела: " + emp.department);
            System.out.println("Заработная плата: " + emp.salary + "\n");
        }
    }

    static void executeAction(String action, Scanner scan) {
        if (action.equals("доб"))
            addData(scan);
        else if (action.equals("изм"))
            changeData(scan);
        else if (action.equals("уд"))
            deleteData(scan);
        else if (action.equals("выв"))
            showData(scan);
        else if (action.equals("сум"))
            showDepartmentSum(scan);
        else if (action.equals("выход"))
            System.exit(0);
    }

    public static void main(String[] args) {
        System.out.println("Данная программа позволяет работать со сведениями о месячной заработной плате сотрудников отдела.\n");
        Scanner scan = new Scanner(System.in);
        showInfo();
        String action = chooseAction(scan);
        executeAction(action, scan);
    }
}