import docx

def get_text(filename):
    doc = docx.Document(filename)
    fullText = []
    for para in doc.paragraphs:
        fullText.append(para.text)
    return '\n'.join(fullText)

def take_first_word(total_text):
    all_words = ""
    for line in total_text.splitlines():
        for word in line.split(" "):
            all_words += word + "\n"
            break
    return all_words
            
def write_into_text_file(text, filename):
    file = open(filename, "w")
    for line in text.split():
        print(line)
        file.writelines(line + "\n")
    file.close()

def add_quotation_marks_and_comma_to_textfile(filename, new_filename):
    file = open(filename, "r")
    file2 = open(new_filename, "w")
    for line in file.readlines():
        file2.writelines('"'+line[:-1]+'"'+","+"\n")

if __name__=="__main__":
    total_text = get_text("all.docx")
    first_words = take_first_word(total_text)
    write_into_text_file(first_words, "english_words.txt")
    add_quotation_marks_and_comma_to_textfile("english_words.txt", "english_words_new.txt")
