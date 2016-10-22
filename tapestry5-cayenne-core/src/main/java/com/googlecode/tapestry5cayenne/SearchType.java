package com.googlecode.tapestry5cayenne;


/**
 * Search types possible for EntityField auto-completion.
 * PREFIX: field like 'input%';
 * SUFFIX: field like '%input';
 * SOUNDEX: assumes that the search fields are already in soundex form. Computes the soundex value of the input, and searches for matches in the search field.
 * ANYWHERE: field like '%input%';
 * @author robertz
 *
 */
public enum SearchType {
    /**
     * Search performed as: field LIKE 'input%'
     */
PREFIX {
    public String maskInput(String input){
        if (input == null || "".equals(input)) {
            return input;
        }
        return input + "%";
    }  
},
/**
 * Search performed as: field LIKE '%input'
 */
SUFFIX {
  public String maskInput(String input) {
      if (input == null || "".equals(input)) {
          return input;
      }
      return "%" + input;
  }
},
/**
 * Search performed as: field LIKE '%input%'
 */
ANYWHERE,//default.
/**
 * Search performed as:field LIKE soundex(input);
 * Where soundex is computed as in "The Art of Computing" pgs. 372-373.
 * It is assumed that the field(s) being compared are already in soundex-form.
 */
SOUNDEX {
  public String maskInput(String input) {
      //perform the soundex algorithm...
      StringBuilder buf = new StringBuilder();
      if (input == null || input.length() == 0) {
          return input;
      }
      buf.append(Character.toUpperCase(input.charAt(0)));
      boolean lastWasVowel=true;
      for(int i=1;i<input.length();i++) {
          //ok... 
          char c = Character.toUpperCase(input.charAt(i));
          switch(c) {
              case 'B':
              case 'F':
              case 'P':
              case 'V':
                  if (lastWasVowel || buf.charAt(buf.length()-1) != '1') {
                      buf.append('1');
                      lastWasVowel=false;
                  }
                  break;
              case 'C':
              case 'G':
              case 'J':
              case 'K':
              case 'Q':
              case 'S':
              case 'X':
              case 'Z':
                  if (lastWasVowel || buf.charAt(buf.length()-1) != '2') {
                      buf.append('2');
                      lastWasVowel=false;
                  }
                  break;
              case 'D':
              case 'T':
                  if (lastWasVowel || buf.charAt(buf.length()-1) != '3') {
                      buf.append('3');
                      lastWasVowel=false;
                  }
                  break;
              case 'L':
                  if (lastWasVowel || buf.charAt(buf.length() - 1) != '4') {
                      buf.append('4');
                      lastWasVowel=false;
                  }
                  break;
              case 'M':
              case 'N':
                  if (lastWasVowel || buf.charAt(buf.length()-1) != '5') {
                      buf.append('5');
                      lastWasVowel=false;
                  }
                  break;
              case 'R':
                  if (lastWasVowel || buf.charAt(buf.length()-1) != '6') {
                      buf.append(6);
                      lastWasVowel=false;
                  }
              default : lastWasVowel=true;
          }
      }
      while(buf.length() < 4) {
          buf.append('0');
      }
      return buf.substring(0,4);
  }
};

  public String maskInput(String input) {
      if (input == null || "".equals(input)) {
          return input;
      }
      return "%" + input + "%";
  }
  
}
