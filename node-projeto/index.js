const express = require("express");
const { randomUUID } = require('crypto');

const app = express();
app.get("/produto", (request, response) => {
  return response.json({ mensagem: "teste" });
});
app.post("/produto", (request, response) => {
    const { nome, preco } = request.body;
    console.log(request.body);
    const prod = {
    'nome': nome,
    'preco': preco,
    id: randomUUID()
    }
    produtos.push(prod);
    return response.json(produtos)
})
app.listen(3001, () => console.log("server running on port 3001"));
