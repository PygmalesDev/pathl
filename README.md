# pathl
pathl is a developing object-oriented programming language for JVM based on the works of [Robert Nystrom](https://craftinginterpreters.com/introduction.html). A tokenizer and a parser for simple expressions, as well as some generic tree printers were implemented.
pathl parses .phl files, but can also be interacted with through shell.

pathl features an enchansed error logger similar to Rust:

![изображение](https://github.com/user-attachments/assets/1592e9f2-b647-4ec7-b191-8182969797dc)

## AST Generation
This repository also conations an [AST Generator](https://github.com/PygmalesDev/pathl/blob/main/src/main/java/net/pygmales/util/AstGenerator.java). It produces java Expression and Visitor classes based on a simple syntax for a given context free grammar.

![изображение](https://github.com/user-attachments/assets/7d906fb1-fd69-490d-b999-85a26b6fa756)
