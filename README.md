# Testes Unitários e de Integração com WireMock

## Cenário

Ao trabalhar com microsserviços e integrações entre sistemas, as chamadas HTTP podem dificultar os testes unitários e de integração.
Para resolver isso, é possível utilizar um framework de mock de objetos como o Mockito e Easy Mock, mas é preciso ter cuidado para não ocultar lógicas de negócios ou não realizar testes próximos do real.
Uma solução é o uso do WireMock.

## O que é o [WireMock](https://wiremock.org/)?

É uma ferramenta Java de código aberto para simulação de serviços HTTP para testes. Ele permite a criação de endpoints falsos que respondem a requisições HTTP com respostas predefinidas.
Ele pode ser usado em vários cenários, mas nesta apresentação abordaremos seu uso em projetos Spring Boot para simular serviços externos.

## Como o WireMock funciona?

O WireMock simula um servidor mapeando endpoints e seus verbos HTTP (GET, POST, PUT, etc.), cabeçalhos, corpo da requisição dentre outros recursos e retorna uma resposta predefinida com código de retorno, corpo da resposta e outros recursos necessários para uma resposta HTTP.

## Usando o WireMock em um projeto Spring Boot

Adicionar a dependência do WireMock

```xml
<!-- Maven pom.xml -->
<dependency>
  <groupId>org.wiremock</groupId>
  <artifactId>wiremock</artifactId>
  <version>3.0.1</version>
  <scope>test</scope>
</dependency>
```

Em projetos com JUnit 4 e JUnit 5 Vintage, o WireMock disponibiliza uma _JUnit rule_ que facilita o gerenciamento de uma ou mais instâncias. Além disso, seu ciclo de vida é automatizado, iniciando o servidor antes de cada teste e parando-o após.

```java
public class WireMockTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8888); // Default port (8080)

    @Test
    void testSomethingWithWiremock() {
        // Do some testing...
    }
}
```

Em projetos com JUnit 5+ Jupiter, o WireMock disponibiliza a anotação da classe de teste @WireMockTest que simplifica a execução de uma instância. Além disso, seu ciclo de vida é automatizado em ambos os casos, iniciando o servidor antes do primeiro teste e parando-o após o último teste. Os mocks são resetados a cada teste.

```java
@WireMockTest(httpPort = 8888) // Default port (8080)
public class WireMockTest {
    @Test
    void testSomethingWithWiremock() {
        // Do some testing...
    }
}
```

E para mais que uma instância ou mais configurações, é disponibilizado o WireMockExtension.

```java
public class WireMockTest {
    @RegisterExtension
    public static WireMockExtension wireMock1 = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8888))
            .build();

    @RegisterExtension
    public static WireMockExtension wireMock2 = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8889))
            .build();

    @Test
    void testSomethingWithWiremock() {
        // Do some testing...
    }
}
```

Após a configuração da instância do WireMock, o próximo passo é configurar um stub.

O WireMock possui uma API fluente para construção dos testes usando a classe **WireMock**:

```java
WireMock.stubFor(WireMock.get("/v1/endpoint")
    .willReturn(WireMock.aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{ data: [] }")));
```

Podemos ler o código acima da seguinte forma:

**WireMock.stubFor** = crie um mock de um servidor para o seguinte verbo HTTP.

**WireMock.get** = crie um mock para o verbo GET com o endereço especificado.

**willReturn** = que irá retornar uma resposta HTTP.

**WireMock.aResponse** = crie uma resposta HTTP.

**withStatus** = com um HTTP Status definido.

**withHeader** = com um header de resposta.

**withBody** = com um corpo de resposta específico.

## URL Matching

```java
WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/v1/.*"))
    .willReturn(WireMock.aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{ data: [] }")));
```

Podemos ler o código acima da seguinte forma:

**WireMock.stubFor** = crie um mock de um servidor para o seguinte verbo HTTP.

**WireMock.get** = crie um mock para o verbo GET com o endereço especificado.

**WireMock.urlPathMatching** = com um caminho de URL que corresponda ao padrão especificado.

**willReturn** = que irá retornar uma resposta HTTP.

**WireMock.aResponse** = crie uma resposta HTTP.

**withStatus** = com um HTTP Status definido.

**withHeader** = com um header de resposta.

**withBody** = com um corpo de resposta específico.

## Request Header Matching

```java
WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/v1/endpoint"))
		.withHeader("Accept", WireMock.matching("text/.*"))
    .willReturn(WireMock.aResponse()
        .withStatus(503)
        .withHeader("Content-Type", "application/json")
        .withBody("{ error: \"Service Unavailable\" }")));
```

Podemos ler o código acima da seguinte forma:

**WireMock.stubFor** = crie um mock de um servidor para o seguinte verbo HTTP.

**WireMock.get** = crie um mock para o verbo GET com o endereço especificado.

**WireMock.urlPathEqualTo** = com um caminho de URL igual ao especificado.

**withHeader** = com um cabeçalho de solicitação específico.

**WireMock.matching** = que corresponda ao padrão especificado.

**willReturn** = que irá retornar uma resposta HTTP.

**WireMock.aResponse** = crie uma resposta HTTP.

**withStatus** = com um HTTP Status definido.

**withHeader** = com um header de resposta.

**withBody** = com um corpo de resposta específico.

## Request Body Matching

```java
WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/v1/endpoint"))
		.withHeader("Content-Type", WireMock.equalTo("application/json"))
    .withRequestBody(WireMock.containing("\"testing-library\": \"WireMock\""))
    .willReturn(WireMock.aResponse()
        .withStatus(200)));
```

Podemos ler o código acima da seguinte forma:

**WireMock.stubFor** = crie um mock de um servidor para o seguinte verbo HTTP.

**WireMock.post** = crie um mock para o verbo POST com o endereço especificado.

**WireMock.urlEqualTo** = com um URL igual ao especificado.

**withHeader** = com um cabeçalho de solicitação específico.

**WireMock.equalTo** = que seja igual ao valor especificado.

**withRequestBody** = com um corpo de solicitação específico.

**WireMock.containing** = que contenha o texto especificado.

**willReturn** = que irá retornar uma resposta HTTP.

**WireMock.aResponse** = crie uma resposta HTTP.

**withStatus** = com um HTTP Status definido.

```java
WireMock.stubFor(WireMock.put(WireMock.urlEqualTo("/v1/endpoint"))
		.withHeader("Content-Type", WireMock.equalTo("application/json"))
    .withRequestBody(WireMock.containing("\"testing-library\": \"WireMock\""))
    .willReturn(WireMock.aResponse()
        .withStatus(200)));
```

Podemos ler o código acima da seguinte forma:

**WireMock.stubFor** = crie um mock de um servidor para o seguinte verbo HTTP.

**WireMock.put** = crie um mock para o verbo PUT com o endereço especificado.

**WireMock.urlEqualTo** = com um URL igual ao especificado.

**withHeader** = com um cabeçalho de solicitação específico.

**WireMock.equalTo** = que seja igual ao valor especificado.

**withRequestBody** = com um corpo de solicitação específico.

**WireMock.containing** = que contenha o texto especificado.

**willReturn** = que irá retornar uma resposta HTTP.

**WireMock.aResponse** = crie uma resposta HTTP.

**withStatus** = com um HTTP Status definido.

## Exemplo com verbo DELETE

```java
WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo("/v1/endpoint"))
		.withHeader("Content-Type", WireMock.equalTo("application/json"))
    .willReturn(WireMock.aResponse()
        .withStatus(204)));
```

Podemos ler o código acima da seguinte forma:

**WireMock.stubFor** = crie um mock de um servidor para o seguinte verbo HTTP.

**WireMock.delete** = crie um mock para o verbo DELETE com o endereço especificado.

**WireMock.urlEqualTo** = com um URL igual ao especificado.

**withHeader** = com um cabeçalho de solicitação específico.

**WireMock.equalTo** = que seja igual ao valor especificado.

**willReturn** = que irá retornar uma resposta HTTP.

**WireMock.aResponse** = crie uma resposta HTTP.

**withStatus** = com um HTTP Status definido.

## Stub Priority

```java
WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/v1/.*"))
    .atPriority(1)
    .willReturn(WireMock.aResponse()
        .withStatus(200)));
WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/v1/endpoint"))
    .atPriority(2)
		.withHeader("Accept", WireMock.matching("text/.*"))
    .willReturn(WireMock.aResponse()
        .withStatus(503)));
```

O **`stub priority`** do WireMock é usado para especificar a ordem em que os stubs são avaliados. Quando várias regras de stub correspondem a uma solicitação, a regra com a prioridade mais alta (menor valor numérico) é selecionada e sua resposta é retornada.

No exemplo, existem duas regras de stub para solicitações GET, com prioridades 1 e 2. A primeira regra tem prioridade mais alta e sua resposta será retornada se ambas as regras corresponderem à solicitação.

💡 [Documentação](https://wiremock.org/docs/)

## Aplicação de exemplo testada com WireMock

A finalidade principal da aplicação é atuar como um comparador de preços de criptomoedas entre as corretoras Binance e Mercado Bitcoin, utilizando suas APIs públicas.

O WireMock é empregado para simular os serviços dessas corretoras durante os testes.

💡 Repositório da aplicação no [Github](https://github.com/silvaadriel/crypto-price-compare)

- **Testes unitários** para o **Client**:
  - [crypto-price-compare/src/test/java/com/example/cryptopricecompare /integration/](https://github.com/silvaadriel/crypto-price-compare/tree/main/src/test/java/com/example/cryptopricecompare/integration)
- **Testes de integração** para a camada de **Service**:
  - [crypto-price-compare/src/test/java/com/example/cryptopricecompare /service/](https://github.com/silvaadriel/crypto-price-compare/tree/main/src/test/java/com/example/cryptopricecompare/service)
- **Testes de integração** para a camada de **Controller**:
  - [crypto-price-compare/src/test/java/com/example/cryptopricecompare/web /v1/](https://github.com/silvaadriel/crypto-price-compare/tree/main/src/test/java/com/example/cryptopricecompare/web/v1)

## Referências

- [Usando WireMock para acelerar Testes Unitários e Integrados com Spring Boot e API Oficial da Marvel](https://thomsdacosta.medium.com/usando-wiremock-para-acelerar-testes-unit%C3%A1rios-e-integrados-com-spring-boot-e-api-oficial-da-marvel-b26d06d61552)
- [Introduction to WireMock](https://www.baeldung.com/introduction-to-wiremock)
- [Quick Start: API Mocking with Java and JUnit 4](https://wiremock.org/docs/quickstart/java-junit/)
- [JUnit 4 and Vintage](https://wiremock.org/docs/junit-extensions/)
- [JUnit 5+ Jupiter](https://wiremock.org/docs/junit-jupiter/)
