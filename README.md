<h1 align=center>Projeto Vem Ser DBC - 9ª Edição</h1>

<img src="https://user-images.githubusercontent.com/92181625/174544191-09485209-4752-4143-b750-688e5cbabe91.png">

<h2>Seja Bem Vindo ao Back-End da API Colabore</h2>
<h1 align="center">🎁📚🎀🎁📚🎀🎁📚🎀🎁📚🎀🎁📚🎀🎁📚🎀🎁📚</h1>

<h2> Sobre este Projeto </h2>

<p>O Colabore trata-se de um Sistema para Divulgação de Campanhas Colaborativas. O software foi elaborado com arquitetura API Rest, documentação feita com Swagger e deploy no Heroku. 
O objetivo do projeto é permitir que os colaboradores da DBC possam participar e se engajar em causas coletivas como arrecadação de dinheiro para páscoa solidária, arrecadação para comprar livros de tecnologia, etc.</p>
<p>Realizamos o desenvolvimento do CRUD (Create-Read-Update-Delete) de usuários, campanhas, tags, doadores, etc., onde as informações ficam dentro de uma base de dados Postgres.
<p>Executamos consultas no banco de dados através de Query's JPQL (Java Persistence Query Language) presentes na camada de Repository garantindo assim, maior performance para a aplicação. Também implementamos diversas outras ferramentos como: Spring Security (Token JWT, Bcrypt, Regras Específicas), Spring Web (lombok, object mapper, injeções de dependências, validation), etc.</p>
<p>Esta API trabalha com chaves primarias e estrangeiras, garantindo total integridade da aplicação.</p>
<p>Por tratar-se de uma API Rest ela estabelece uma comunicação stateless entre cliente e servidor. Isso significa que nenhuma informação do cliente é armazenada entre solicitações GET e todas as solicitações são separadas e desconectadas.</p>
<h2>Diagrama de Entidade Relacionamento</h2>
<img src="https://user-images.githubusercontent.com/92181625/185804429-f437b2ec-151e-4028-a599-79829f27969c.png">

<h2>📁 Diretórios e arquivos</h2>

```
.
├───.mvn
│   └───wrapper
├───Colabore-API
│   └───.idea
│       └───libraries
├───src
│   ├───main
│   │   ├───java
│   │   │   └───br
│   │   │       └───com
│   │   │           └───dbccompany
│   │   │               └───colaboreapi
│   │   │                   ├───config
│   │   │                   ├───controller
│   │   │                   ├───dto
│   │   │                   │   ├───autenticacao
│   │   │                   │   ├───campanha
│   │   │                   │   ├───doador
│   │   │                   │   ├───tag
│   │   │                   │   └───usuario
│   │   │                   ├───entity
│   │   │                   ├───enums
│   │   │                   ├───exceptions
│   │   │                   ├───repository
│   │   │                   ├───security
│   │   │                   └───service
│   │   └───resources
│   │       ├───static
│   │       └───templates
│   └───test
│       └───java
│           └───br
│               └───com
│                   └───dbccompany
│                       └───colaboreapi
│                           └───service
└───target
    ├───classes
    │   └───br
    │       └───com
    │           └───dbccompany
    │               └───colaboreapi
    │                   ├───config
    │                   ├───controller
    │                   ├───dto
    │                   │   ├───autenticacao
    │                   │   ├───campanha
    │                   │   ├───doador
    │                   │   ├───tag
    │                   │   └───usuario
    │                   ├───entity
    │                   ├───enums
    │                   ├───exceptions
    │                   ├───repository
    │                   ├───security
    │                   └───service
    ├───generated-sources
    │   └───annotations
    ├───generated-test-sources
    │   └───test-annotations
    ├───maven-archiver
    ├───maven-status
    │   └───maven-compiler-plugin
    │       ├───compile
    │       │   └───default-compile
    │       └───testCompile
    │           └───default-testCompile
    └───test-classes
        └───br
            └───com
                └───dbccompany
                    └───colaboreapi
                        └───service

```
<h2>Gostaria de Falar Conosco?</h2>
<address>
Clique no nome dos desenvolvedores para enviar um e-mail
<br>
<br>
<a href="mailto:cesar.desenvolvedor@gmail.com">Cesar</a>
<br>
<br>
<a href="mailto:prfultra@yahoo.com.br">Paulo</a> 
</address>
<h2>Acessando a API</h2>
Para acessar esta API, clique no seguinte endereço: https://colabore-dbc-api.herokuapp.com/swagger-ui/index.html#/
<h2>Clonando este Projeto</h2>
<p>Para clonar este projeto basta digitar o seguinte comando em seu terminal git<p>
<ul>
  <li> git clone https://github.com/Paulo-Ultra/Colabore-DBC
</ul>
<h2>Instalando</h2>  
<p>Para melhor explorar, alterar, incrementar este projeto, sugerimos a instalação do próprio <a href="https://www.jetbrains.com/pt-br/idea/download/#section=windows">IntelliJ</a> para abrir os arquivos. IDE utilizada
  para construção deste sistema. Nela você encontra-ra diversas ferramentas e recursos para explorar e evoluir ainda mais essa aplicação</p>
<hr>
<h2>Gostaria de visitar nossa camada de front-end?</h2>  
<ul>
  <p>Repositório</p>
  <li>https://github.com/Antochevis/Colabore-DBC</li>
  <br>
  <p>Deploy no Vercel</p>
  <li>https://colabore-dbc.vercel.app/</li>
</ul>
<hr>
<h4 align="center">Agradecemos sua Visita ! 😀</h4>
