import PyPDF2
from fpdf import FPDF
import sys
import re
import pdb
import os

#Usage: python3 uzupizer.py number color plant animal word applicant_name date
rx = re.compile('[\W\.]+')
for s in range(len(sys.argv)):
    sys.argv[s] = rx.sub(' ', sys.argv[s]).strip()
    print sys.argv[s]

path = "/home/roboy/cognition_ws/src/roboy_cognition/roboy_dialog/resources/scripts/"

file = open(path+'counter.txt', 'r+')
counter = int(file.readline())
counter += 1
file.seek(0)
file.write(str(counter))
file.truncate()
file.close()
# pdb.set_trace()

pdf = FPDF(format='a4')
pdf.add_page()
pdf.set_font("Arial", size=10,style='B')
pdf.text(162,43,"No. 0000"+str(counter))
pdf.set_font("Arial", size=10)
pdf.text(57,82,sys.argv[2])
pdf.text(57,89,sys.argv[3])
pdf.text(57,96,sys.argv[4])
pdf.text(57,103,sys.argv[5])
pdf.set_font("Arial", size=10,style='B')
pdf.text(57,144,sys.argv[6])
pdf.text(57,169,"February, 23 2018")
pdf.output(path+"tmp.pdf")


pdf_tpl = PyPDF2.PdfFileReader(open(path+"Uzupis_Naturalization_Certificate_Template.pdf", "rb"))
pdf_merge = PyPDF2.PdfFileReader(open(path+"tmp.pdf","rb"))
pdf_tpl.pages[0].mergePage(pdf_merge.pages[0])

pdf_out = PyPDF2.PdfFileWriter()
pdf_out.addPage(pdf_tpl.pages[0])
with open(path+"Uzupis_Naturalization_Certificate_"+str(counter)+".pdf", "wb") as outputStream:
	pdf_out.write(outputStream)

outstr = "lp -d printer "+path+"Uzupis_Naturalization_Certificate_"+str(counter)+".pdf"
print outstr
os.system(outstr)
