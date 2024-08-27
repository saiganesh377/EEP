from bs4 import BeautifulSoup

# Load the HTML content from the file
with open('your_file.html', 'r') as file:
    html_content = file.read()

# Parse the HTML content
soup = BeautifulSoup(html_content, 'html.parser')

# Find all <td> tags with width containing "%"
for td in soup.find_all('td', width=lambda x: x and '%' in x):
    if td['width'] == '5%':
        line_number = td.text.strip()  # Fetch the line number
    else:
        file_path = td.text.strip()
        # Construct the hyperlink with the file path and line number
        hyperlink = f'<a href="vscode://file/{file_path}:{line_number}">{file_path}</a>'
        td.string = ''  # Clear the existing text content
        td.append(BeautifulSoup(hyperlink, 'html.parser'))  # Add the new hyperlink

# Save the modified HTML back to the file
with open('your_file_modified.html', 'w') as file:
    file.write(str(soup))