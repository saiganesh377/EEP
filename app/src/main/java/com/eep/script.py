from bs4 import BeautifulSoup

# Step 1: Parse the PMD HTML report
with open('pmd_report.html', 'r') as file:
    soup = BeautifulSoup(file, 'html.parser')

# Step 2: Find all rows containing the data
for row in soup.find_all('tr')[1:]:  # Skips the header row
    columns = row.find_all('td')
    line_number = int(columns[1].text.strip())
    file_name = columns[2].text.strip()

    # Step 3: Read the specific line from the source file
    try:
        with open(file_name, 'r') as source_file:
            lines = source_file.readlines()
            line_content = lines[line_number - 1].strip()  # Adjust for 0-based indexing
    except Exception as e:
        line_content = f"Error: {str(e)}"  # Handle cases where the file or line might not exist

    # Step 4: Add a new column with the line content
    new_column = soup.new_tag('td')
    new_column.string = line_content
    row.append(new_column)

# Step 5: Update the header to include the new column name
header_row = soup.find('tr')
new_header = soup.new_tag('th')
new_header.string = "Line Content"
header_row.append(new_header)

# Step 6: Save the updated HTML report
with open('updated_pmd_report.html', 'w') as file:
    file.write(str(soup))