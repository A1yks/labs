#include <iostream>
#include <vector>
#include <algorithm>
#include <fstream>
#include <cstring>

using namespace std;

struct Employee {
public:
    char surname[20];
    char department[20];
    double salary;

    Employee() {
        strcpy(surname, "");
        strcpy(department, "");
        salary = 0;
    }

    Employee(char surname[20], char department[20], double salary) {
        strcpy(this->surname, surname);
        strcpy(this->department, department);
        this->salary = salary;
    }
};

struct DepartmentData {
public:
    string name;
    double sum = 0;

    DepartmentData(string name, double sum) {
        this->name = name;
        this->sum = sum;
    }
};

void executeAction(string action);

void showInfo() {
    cout << "Выберите, что вы хотите сделать (введите в консоль значение из квадратных скобок):" << endl;
    cout << "| Добавить сотрудников [доб] | Изменить данные сотрудников [изм] | Удалить сотрудников [уд] | Вывести информацию о сотрудниках на экран [выв] | Вывести сумму выплат по отделам за месяц [сум] | Завершить работу программы [выход] |" << endl;
}

string chooseAction() {
    vector<string> actions = {"доб", "изм", "уд", "выв", "сум", "выход"};
    string choice;
    getline(cin, choice);
    while(find(actions.begin(), actions.end(), choice) == actions.end()) {
        cout << "Вы должны выбрать существующее действие." << endl;
        getline(cin, choice);
    }
    return choice;
}

void showArr(vector<Employee> data) {
    for(int i = 0; i < data.size(); i++) {
        Employee emp = data[i];
        cout << "Сотрудник №" << (i+1) << endl;
        cout << "Фамилия: " << emp.surname << endl;
        cout << "Наименование отдела: " << emp.department << endl;
        cout << "Заработная плата: " << emp.salary << '\n' << endl;
    }
}

vector<Employee> grabData(bool show, string *fp = nullptr) {
    string filePath;
    if(fp == nullptr) {
        cout << "Введите имя файла" << endl;
        getline(cin, filePath);
        filePath += ".dat";
    } else
        filePath = *fp;
    vector<Employee> data;
    ifstream is(filePath);
    if(is.good()) {
        Employee temp;
        while(is.read((char*)&temp, sizeof(Employee))) {
            Employee emp(temp.surname, temp.department, temp.salary);
            data.push_back(emp);
        }
        if(show) {
            if(data.size() > 0) {
                cout << "Данные, записанные в файле:" << endl;
                showArr(data);
            } else
                cout << "В файле не найдено ни одной записи" << endl;
        }
    } else
        cout << "Произошла ошибка при чтении файла. Убедитесь, что такой файл существует." << endl;
    is.close();
    return data;
}

void createNewFile(vector<Employee> data, string filePath) {
    ofstream os(filePath, ifstream::binary);
    if(os.is_open()) {
        for(Employee emp : data)
            os.write((char*)&emp, sizeof(Employee));
        cout << "Данные успешно сохранены в файл " << filePath << endl;
    } else {
        cout << "Не удалось сохранить данные в файл." << endl;
    }
    os.close();
}

void updateFile(vector<Employee> newData, string filePath) {
    vector<Employee> data = grabData(false, &filePath);
    data.insert(data.end(), newData.begin(), newData.end());
    ofstream os(filePath, ifstream::binary);
    if(os.is_open()) {
        for(Employee emp : data)
            os.write((char*)&emp, sizeof(Employee));
        cout << "Данные успешно обновлены в файле " << filePath << endl;
    } else {
        cout << "Не удалось сохранить данные в файл." << endl;
    }
    os.close();
}

void saveDataToFile(vector<Employee> data, bool update) {
    cout << "Введите [д], если хотите сохранить данные в файл." << endl;
    string choice;
    getline(cin, choice);
    if(choice == "д") {
        cout << "Введите имя файла" << endl;
        string filePath;
        getline(cin, filePath);
        filePath += ".dat";
        ifstream file (filePath);
        if(update && file.good()) {
            cout << "Файл с таким именем уже существует. Хотите его обновить? [д/н] (При выборе [н] создастся новый файл)" << endl;
            getline(cin, choice);
            if(choice == "д")
                updateFile(data, filePath);
            else
                createNewFile(data, filePath);
        } else
            createNewFile(data, filePath);
    }
}

void addData() {
    vector<Employee> data;
    bool continueCycle = true;
    bool notCorrect = true;
    cout << "Если хотите прекратить ввод, введите [стоп] вместо фамилии сотрудника" << endl;
    while(continueCycle) {
        char surname[20];
        char department[20];
        double salary = 0;
        while(notCorrect) {
            cout << "Введите фамилию сотрудника" << endl;
            cin.getline(surname, 20);
            if(surname[0] != '\0') {
                if(strcmp(surname, "стоп") == 0) {
                    continueCycle = false;
                    notCorrect = false;
                } else {
                    cout << "Введите название отдела" << endl;
                    cin.getline(department, 20);
                    if(department[0] != '\0') {
                        cout << "Введите заработную плату сотрудника" << endl;
                        string str;
                        getline(cin, str);
                        try {
                            salary = stod(str);
                            if(salary > -1)
                                notCorrect = false;
                            else {
                                notCorrect = true;
                                cout << "Заработная плата не может быть отрицательной, повторите ввод" << endl;
                            }
                        } catch(...) {
                            notCorrect = true;
                            cout << "Возникла ошибка при вводе данных, повторите ввод." << endl;
                        }
                    } else
                        cout << "Название отдела не может быть пустым, повторите ввод." << endl;
                }
            } else
                cout << "Фамилия не может быть пустой, повторите ввод." << endl;
        }
        if(strcmp(surname, "стоп") != 0) {
            notCorrect = true;
            data.push_back(*new Employee(surname, department, salary));
        }
    }
    saveDataToFile(data, true);
    showInfo();
    executeAction(chooseAction());
}

string makeChoice(vector<string> choices) {
    string choice = "";
    while(find(choices.begin(), choices.end(), choice) == choices.end()) {
        cout << "Введите либо [д], либо [н]" << endl;
        getline(cin, choice);
    }
    return choice;
}

int changeEmployeeData(Employee *emp, int changes) {
    cin.clear();
    string choice = "";
    bool notCorrect = true;
    vector<string> choices = {"д", "н"};
    while(notCorrect) {
        cout << "Хотите изменить фамилию?" << endl;
        choice = makeChoice(choices);
        if (choice == "д") {
            cout << "Введите новую фамилию" << endl;
            char surname[20];
            cin.getline(surname, 20);
            if(surname[0] != '\0') {
                strcpy(emp->surname, surname);
                cout << "Фамилия успешно изменена, хотите продолжить изменение данных?" << endl;
                notCorrect = false;
                changes++;
                choice = makeChoice(choices);
                if (choice == "д")
                    changeEmployeeData(emp, changes);
            } else
                cout << "Фамилия не может быть пустой, повторите ввод." << endl;
        } else {
            cout << "Хотите изменить наименование отдела?" << endl;
            choice = makeChoice(choices);
            if (choice == "д") {
                cout << "Введите наименование отдела" << endl;
                char department[20];
                cin.getline(department, 20);
                if(department[0] != '\0') {
                    strcpy(emp->department, department);
                    cout << "Наименование отдела успешно изменено, хотите продолжить изменение данных?" << endl;
                    notCorrect = false;
                    changes++;
                    choice = makeChoice(choices);
                    if (choice == "д")
                        changeEmployeeData(emp, changes);
                } else
                    cout << "Название отдела не может быть пустым, повторите ввод." << endl;
            } else {
                cout << "Хотите изменить размер заработной платы?" << endl;
                choice = makeChoice(choices);
                if (choice == "д") {
                    cout << "Введите размер заработной платы" << endl;
                    double salary;
                    string str;
                    try {
                        getline(cin, str);
                        salary = stod(str);
                        if (salary > -1) {
                            notCorrect = false;
                            emp->salary = salary;
                            cout << "Заработная плата успешно изменена" << endl;
                            changes++;
                        } else {
                            notCorrect = true;
                            cout << "Заработная плата не может быть отрицательной, повторите ввод" << endl;
                        }
                    } catch (...) {
                        cout << "Заработная плата должна быть числом" << endl;
                    }
                }
            }
        }
    }
    return changes;
}

void changeData() {
    vector<Employee> data = grabData(true);
    if(data.size() > 0) {
        bool continueCycle = true;
        bool notCorrect = true;
        int changedCount = 0;
        while(continueCycle) {
            cout << "Введите номер сотрудника, данные которого хотите изменить, либо [стоп], чтобы вернуться в меню" << endl;
            while(notCorrect) {
                string input;
                getline(cin, input);
                string choice = "";
                if(input == "стоп") {
                    notCorrect = false;
                    continueCycle = false;
                } else {
                    try {
                        int changeNum = stoi(input);
                        if(changeNum <= data.size() && changeNum > 0) {
                            Employee *emp = &data[changeNum-1];
                            changedCount = changeEmployeeData(emp, 0);
                            notCorrect = false;
                        } else
                            cout << "Сотрудник с таким номером не найден, повторите ввод." << endl;
                    } catch (...) {
                        cout << "Номер сотрудника должен быть числом, повторите ввод." << endl;
                    }
                }
            }
            notCorrect = true;
        }
        if(changedCount > 0)
            saveDataToFile(data, false);
    }
    showInfo();
    executeAction(chooseAction());
}

void deleteData() {
    vector<Employee> data = grabData(true);
    if(data.size() > 0) {
        bool continueCycle = true;
        bool notCorrect = true;
        int deletedCount = 0;
        while(continueCycle) {
            cout << "Введите номер сотрудника, которого хотите удалить, либо [стоп], чтобы вернуться в меню" << endl;
            while(notCorrect) {
                string input;
                getline(cin, input);
                if(input == "стоп") {
                    notCorrect = false;
                    continueCycle = false;
                } else {
                    try {
                        int deleteNum = stoi(input);
                        if(deleteNum > 0 && deleteNum <= data.size()) {
                            data.erase(data.begin()+deleteNum-1);
                            deletedCount++;
                            cout << "Сотрудник № " << deleteNum << " успешно удален." << endl;
                            notCorrect = false;
                        } else {
                            cout << "Сотрудник с таким номером не найден, повторите ввод." << endl;
                            notCorrect = true;
                        }
                    } catch (...) {
                        cout << "Номер сотрудника должен быть числом." << endl;
                        notCorrect = true;
                    }
                }
            }
            notCorrect = true;
        }
        if(deletedCount > 0)
            saveDataToFile(data, false);
    }
    showInfo();
    executeAction(chooseAction());
}

void showData() {
    grabData(true);
    showInfo();
    executeAction(chooseAction());
}

DepartmentData* grabCurrentDepartment(string departmentName, vector<DepartmentData> &departments) {
    bool isInArr = false;
    int i = 0;
    DepartmentData *department = nullptr;
    while(!isInArr && i < departments.size()) {
        DepartmentData *temp = &departments[i];
        if(temp->name == departmentName) {
            isInArr = true;
            department = temp;
        }
        i++;
    }
    return department;
}

vector<DepartmentData> grabDepartments(vector<Employee> data) {
    vector<DepartmentData> departments;
    for(Employee emp : data) {
        DepartmentData *department = grabCurrentDepartment(emp.department, departments);
        if(department == nullptr)
            departments.push_back(*new DepartmentData(emp.department, emp.salary));
        else
            department->sum += emp.salary;
    }
    return departments;
}

void sortDepartments(vector<DepartmentData> &departments) {
    for(int i = 0; i < departments.size(); i++) {
        for(int j = 1; j < departments.size()-i; j++) {
            DepartmentData *prev = &departments[j-1];
            DepartmentData *curr = &departments[j];
            char currName[curr->name.size()];
            char prevName[prev->name.size()];
            strcpy(currName, curr->name.c_str());
            strcpy(prevName, prev->name.c_str());
            if(strcmp(currName, prevName) < 0)
                swap(departments[j], departments[j-1]);
        }
    }
}

void showDepartmentSum() {
    vector<Employee> data = grabData(false);
    vector<DepartmentData> departments = grabDepartments(data);
    sortDepartments(departments);
    for(DepartmentData department : departments) {
        cout << department.name << endl;
        cout << "Общая сумма выплат за месяц по отделу = " << department.sum << '\n' << endl;
    }
    showInfo();
    executeAction(chooseAction());
}

void executeAction(string action) {
    if(action == "доб")
        addData();
    else if(action == "изм")
        changeData();
    else if(action == "уд")
        deleteData();
    else if(action == "выв")
        showData();
    else if(action == "сум")
        showDepartmentSum();
    else if(action == "выход")
        exit(0);
}

int main() {
    setlocale(LC_ALL,".1251");
    cout << "Данная программа позволяет работать со сведениями о месячной заработной плате сотрудников отдела.\n" << endl;
    showInfo();
    string action = chooseAction();
    executeAction(action);
    return 0;
}