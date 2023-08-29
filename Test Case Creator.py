# Importing required libraries
import random
import string
import csv
import os

# Function to generate random string based on test type
def random_string(min_length, max_length, character_set, special_chars=False, unicode_chars=False):
    length = random.randint(min_length, max_length)
    if special_chars:
        character_set += string.punctuation
    if unicode_chars:
        character_set += "éüö"
    
    return ''.join(random.choice(character_set) for i in range(length))

# Function to generate a single test case
def generate_test_case(num_records, min_length, max_length, character_set, special_chars, unicode_chars, file_name):
    with open(file_name, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(["ID", "Book Title", "Author"])
        
        for i in range(num_records):
            random_title = random_string(min_length, max_length, character_set, special_chars, unicode_chars)
            random_author = random_string(min_length, max_length, character_set, special_chars, unicode_chars)
            writer.writerow([i, random_title, random_author])

# Various testing scenarios
scenarios = [
    {'desc': 'special_chars', 'special_chars': True, 'unicode_chars': False},
    {'desc': 'empty_fields', 'special_chars': False, 'unicode_chars': False},  # Handle empty fields during file write
    {'desc': 'duplicates', 'special_chars': False, 'unicode_chars': False},   # Handle duplicates during file write
    {'desc': 'unicode', 'special_chars': False, 'unicode_chars': True},
    {'desc': 'case_sensitive', 'special_chars': False, 'unicode_chars': False}, # Handle case during file write
    {'desc': 'max_length', 'special_chars': False, 'unicode_chars': False},  # Set max length during file write
    {'desc': 'numerical', 'special_chars': False, 'unicode_chars': False},   # Handle numbers during file write
    {'desc': 'whitespaces', 'special_chars': False, 'unicode_chars': False}, # Handle whitespaces during file write
    {'desc': 'unsorted', 'special_chars': False, 'unicode_chars': False},    # Handle order during file write
    {'desc': 'multiline', 'special_chars': False, 'unicode_chars': False}    # Handle multiline during file write
]

for num_records in [100]:  # Reduced for demonstration; you can expand to [100, 1000, 10000]
    for min_length, max_length in [(5, 15)]:  # Reduced for demonstration; you can expand to [(5, 15), (10, 20), (15, 25)]
        for character_set in [string.ascii_letters]:  # Reduced for demonstration; you can expand to various character sets
            for scenario in scenarios:
                file_name = f'test_case_{num_records}_{min_length}_{max_length}_{scenario["desc"]}.txt'
                generate_test_case(num_records, min_length, max_length, character_set, scenario['special_chars'], scenario['unicode_chars'], file_name)
