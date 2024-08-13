# EEP
name: PMD Analysis

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  pmd_analysis:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '17'  # Specify the Java version you are using

      - name: Install PMD
        run: |
          wget https://github.com/pmd/pmd/releases/download/pmd_releases%2F6.53.0/pmd-bin-6.53.0.zip
          unzip pmd-bin-6.53.0.zip
          export PATH=$PATH:$GITHUB_WORKSPACE/pmd-bin-6.53.0/bin

      - name: Run PMD analysis
        run: |
          pmd -d ./src -R category/java/bestpractices.xml -f text -r pmd_report.txt

      - name: Archive PMD report
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: pmd-report
          path: pmd_report.txt