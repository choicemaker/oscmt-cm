Issues with these scripts:

 1) MCI uses an older version of PBlockB.txt than NYSSIS. They differ in the
 characters used to demark tables and views within the sQuery parameter.
 
 2) Log files from Windows are encoded with double-byte text encoding. Java expect UTF-8, so these
 scripts need to be converted to UTF-8. The VIM editor can be used to do this.
 
 (*) Open a file with VIM
 (*) Write the contents to a new file specifying UTF-8
 :w ++enc=utf-8 some_new_file.txt
 
 The output must be to a completely new file; VIM won't change the text encoding of an
 existing file.
