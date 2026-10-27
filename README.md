# 🏁 Sorting Algorithm Performance Evaluator

> Java Swing app that loads a CSV file, lets you pick a numeric column, and compares the execution time of Insertion, Shell, Merge, Quick and Heap Sort, then displays the best-performing algorithm.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![GUI](https://img.shields.io/badge/GUI-Swing-blueviolet)

---

## ✨ What does it do?

Ever wondered which sorting algorithm is *really* the fastest? 🤔
Load a dataset, pick a column, and let five algorithms race! 🏎️💨

1. 📂 **Upload** any `.csv` file
2. 📋 **Choose** a column from the dropdown
3. ▶️ **Click Sort**, and all 5 algorithms sort the same data
4. ⏱️ **See** the execution time of each one
5. 🏆 **Find out** which algorithm won (highlighted in green)

---

## 📸 Screenshot

<img width="1918" height="1017" alt="Screenshot 2026-10-28 001746" src="https://github.com/user-attachments/assets/fe166c12-c136-41b5-85b6-7b24834255b2" />


---

## 🧮 Algorithms Implemented

| # | Algorithm | Average Time | Idea in one line |
|---|-----------|--------------|------------------|
| 1️⃣ | **Insertion Sort** | O(n²) | Insert each item into its place on the left 🃏 |
| 2️⃣ | **Shell Sort** | ~O(n log² n) | Insertion sort on items far apart, shrinking the gap 🐚 |
| 3️⃣ | **Merge Sort** | O(n log n) | Split in half, sort both, merge them ✂️ |
| 4️⃣ | **Quick Sort** | O(n log n) | Pick a pivot, smaller left, bigger right ⚡ |
| 5️⃣ | **Heap Sort** | O(n log n) | Build a max-heap, take the biggest each time 🌳 |

> 💡 **Fun fact:** On large files, Insertion Sort is dramatically slower than the others. Double the data and it takes about 4× longer!

---

## 🛡️ Smart Error Handling

The app handles bad input gracefully instead of crashing:

- ❌ Wrong file type (not `.csv`)
- 📭 Empty file
- 📄 File with headers but no data rows
- 🔤 Non-numeric column selected (e.g. `Name`) shows a clear message
- 🕳️ Blank or invalid cells inside a numeric column are skipped
- 💬 Quoted values like `"Smith, John"` are read correctly

---

## 🗂️ Project Structure

```
SortingApp/
└── src/
    ├── Main.java                  🚀 Starts the app
    ├── MainFrame.java             🎨 The GUI window
    ├── CsvReaderUtil.java         📥 Reads the CSV and extracts columns
    ├── SortingAlgorithms.java     🧠 The 5 sorting algorithms
    └── PerformanceEvaluator.java  ⏱️ Times each sort and finds the winner
```

---

## 🚀 How to Run

### 📋 Requirements
- ☕ **JDK 11 or higher** (built and tested with **Java 17**)
- 💻 VS Code with the *Extension Pack for Java* (or any Java IDE)

### ▶️ Steps (VS Code)
1. 📥 Clone the repo
   ```bash
   git clone https://github.com/Du2002/Sorting-Algorithm-Performance-Evaluator.git
   ```
2. 📂 Open the folder in VS Code
3. 📄 Open `src/Main.java`
4. ▶️ Click **Run** above `public static void main`

### ▶️ Steps (command line)
```bash
cd src
javac *.java
java Main
```

---

## 🎮 How to Use

1. Click **Upload CSV** 📂 and choose your file
2. The dropdown fills with all column names 📋
3. Select a **numeric** column 🔢
4. Click **Sort** ▶️
5. Check the results table and the 🏆 banner at the bottom!

> 📦 Need test data? Download free CSV datasets from [Kaggle](https://www.kaggle.com/datasets).

---

## 🧪 Tested With

- ✅ Small hand-made CSV files
- ✅ Real Kaggle datasets (video game sales)
- ✅ Large files with 50,000+ rows
- ✅ Invalid files, empty files and text columns

---

## 👥 Team

| 👤 Member | 🎯 Contribution |
|-----------|-----------------|
| K.D.C.Sepiyumi | 📥 CSV reading and dataset handling (`CsvReaderUtil.java`) | GUI Window |
| N.AN.K.R.Nishshanka | 🧠 Sorting algorithms (`SortingAlgorithms.java`) |
| S.A.C.N.Manike | ⏱️ Performance evaluation (`PerformanceEvaluator.java`, `MainFrame.java`) |

---

## 🎓 About

🏫 Department of ICT, Faculty of Technology, University of Colombo

---

⭐ *If you found this useful, give it a star!* ⭐
