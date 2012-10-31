from random import randrange, shuffle
class Indenter:
    def __init__(self, out, level=0):
        self.out = out
        self.level = level
    def wl(self, s):
        for i in range(self.level):
            self.out.write("  ")
        self.out.write(str(s) + "\n")
    def indent(self):
        return Indenter(self.out, self.level + 1)

def write_question(ind):
    ind.wl("<question>")
    a = randrange(9) + 1
    b = randrange(9) + 1
    ii = ind.indent()
    ii.wl("<text>What is %d + %d?</text>"%(a,b))
    
    s = a + b
    cs = {s}
    while len(cs)<4:
        cs.add(randrange(19)+1)
    cs = list(cs)
    shuffle(cs)
    for i,ans in enumerate(cs):
        if ans == s: pos = i
        ii.wl("<choice>It is %d</choice>"%(ans,))
    ii.wl("<answer>%d</answer>"%(pos,))
    ind.wl("</question>")
    
def write_categories(ind):
    for i in range(20):
        ind.wl("<category>")
        ii = ind.indent()
        ii.wl("<name>Category %d</name>"%(i+1))
        ii.wl("<file>xml/%d.xml</file>"%(i+1))
        ind.wl("</category>")
        

out = open("assets/categories.xml","w")
out.write('<?xml version="1.0" encoding="UTF-8"?>\n')
ind = Indenter(out)
ind.wl("<categories>")
write_categories(ind)
ind.wl("</categories>")
out.close()
for i in range(20):
    out = open("assets/xml/%d.xml"%(i+1),"w")
    ind = Indenter(out)
    ind.wl("<questions>")
    ii = ind.indent()
    for i in range(50):
        write_question(ii)
    ind.wl("</questions>")
    out.close()
    
