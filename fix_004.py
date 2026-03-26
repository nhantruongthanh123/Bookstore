import re

# Read the file
with open('src/main/resources/db/changelog/changes/004-insert-sample-datas.yaml', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace all variations of is_deleted with false
content = re.sub(r'- column: \{ name: is_deleted, valueBoolean: false \}', 
                 '- column:\n                  name: is_deleted\n                  valueNumeric: 0', content)
content = re.sub(r'name: is_deleted\s+value: false', 
                 'name: is_deleted\n                  valueNumeric: 0', content)
content = re.sub(r'name: is_deleted\s+valueBoolean: false', 
                 'name: is_deleted\n                  valueNumeric: 0', content)

# Write back
with open('src/main/resources/db/changelog/changes/004-insert-sample-datas.yaml', 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed all is_deleted values to use valueNumeric: 0")
