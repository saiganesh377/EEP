from bs4 import BeautifulSoup

# Step 1: Parse the PMD HTML report
with open('pmd_report.html', 'r') as file:
    soup = BeautifulSoup(file, 'html.parser')

# Step 2: Loop through each row containing file and line number
for row in soup.find_all('tr'):  # Adjust based on your table structure
    file_name = row.find('td', class_='filename').text  # Adjust class name as needed
    line_number = int(row.find('td', class_='linenumber').text)

    # Step 3: Read the specific line from the source file
    with open(file_name, 'r') as source_file:
        lines = source_file.readlines()
        line_content = lines[line_number - 1].strip()  # Adjust indexing

    # Step 4: Add a new column with the line content
    new_column = soup.new_tag('td')
    new_column.string = line_content
    row.append(new_column)

# Step 5: Save the updated HTML report
with open('updated_pmd_report.html', 'w') as file:
    file.write(str(soup))