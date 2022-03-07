esquema = [
  //dados da entidade
  entidadeEndereco: Esquema.caracter,
  entidadeNumero: Esquema.caracter,
  entidadeBairro: Esquema.caracter,
  entidadeCep: Esquema.numero,
  entidadeCnpj: Esquema.numero,
  entidadeTelefone: Esquema.numero,
  entidadeEmail: Esquema.caracter,
  entidadeSite: Esquema.caracter,
  //dados da saida
  saidaNumero: Esquema.numero,
  dataSaida: Esquema.caracter,
  natureza: Esquema.caracter,
  almoxarifado: Esquema.caracter,
  organograma: Esquema.caracter,
  organogramaNumero:Esquema.numero,
  responsavel: Esquema.caracter,
  //saida dos itens
  ordem: Esquema.caracter,
  descricaoItem: Esquema.caracter,
  quantidade: Esquema.numero,
  valorUnitario: Esquema.numero,
  unidadeMedida: Esquema.caracter,
  valorTotal: Esquema.numero,
  totalItem: Esquema.numero,
  responsavelRetirada: Esquema.caracter
];

fonte = Dados.dinamico.v2.novo(esquema);

listaNumeroSaida = parametros.p_Saida.valor.split(',')?:0
p_Almoxarifado = parametros?.p_Almoxarifado?.selecionado?.valor?:nulo
p_ConfigOrganograma = parametros.p_ConfigOrganograma?.selecionado?.valor?:nulo
p_Organograma = parametros?.p_Organograma?.selecionado?.valor?:nulo
p_Dataini = parametros?.p_Dataini?.valor?:nulo
p_Datafin = parametros?.p_Datafin?.valor?:nulo



linha = ""
saidaNumero = []
criterioAlmoxarifado = "almoxarifado.id = $p_Almoxarifado";

se (p_Dataini != nulo) {
  dataini = Datas.dataHora(p_Dataini.ano, p_Dataini.mes, p_Dataini.dia, 00, 00);
  criterioAlmoxarifado += " and dataHoraMovimento >= '" + dataini.formatar('yyyy-MM-dd 00:00:00') + "'";
}

se (p_Datafin != nulo) {
  datafin = Datas.dataHora(p_Datafin.ano, p_Datafin.mes, p_Datafin.dia, 23, 59);
  criterioAlmoxarifado += " and dataHoraMovimento <= '" + datafin.formatar('yyyy-MM-dd 23:59:59') + "'";
}

criterioAlmoxarifado += " and tipoMovimento = 'SAIDA'"

fonteMovimentoAlmoxarifado = Dados.almoxarifado.v1.movimentoAlmoxarifado;

dadosMovimentoAlmoxarifado = fonteMovimentoAlmoxarifado.busca(criterio: criterioAlmoxarifado ,campos: "entidade(id, endereco, cep, numero, telefone, site, email, bairro, cnpj),saidaMaterial.almoxarifado.responsavel.pessoa.nome, saidaMaterial.naturezaMovimentacao.tipo, saidaMaterialItem.saidaMaterial.responsavel.pessoa.nome, valorUnitarioMovimento, valorTotalMovimento, saidaMaterialItem.saidaMaterial.valorTotalItens,tipoMovimento, id, dataHoraMovimento,saidaMaterial.dataSaida,valorTotalMovimento, quantidadeMovimento,saidaMaterial.numeroSaida,saidaMaterial.id,saidaMaterialItem.id, organograma(id,descricao,numero), material(descricao),saidaMaterialItem.numeroItem, materialEspecificacao(unidadeMedida(simbolo)), almoxarifado(organograma(configuracao(id,descricao))),almoxarifado(id, descricao, responsavel(pessoa(nome)))",ordenacao: "almoxarifado.descricao,organograma.id, material.descricao asc")


percorrer (dadosMovimentoAlmoxarifado) { itemMovimentoAlmoxarifado ->
  if(listaNumeroSaida.contains(itemMovimentoAlmoxarifado.saidaMaterial.numeroSaida.toString())) {
    
    ordem = itemMovimentoAlmoxarifado.saidaMaterialItem.numeroItem
    descricaoItem = itemMovimentoAlmoxarifado.material.descricao
    quantidade = itemMovimentoAlmoxarifado.quantidadeMovimento
    valorUnitario = itemMovimentoAlmoxarifado.valorUnitarioMovimento
    unidadeMedida = itemMovimentoAlmoxarifado.materialEspecificacao.unidadeMedida.simbolo
    responsavelRetirada = itemMovimentoAlmoxarifado.saidaMaterialItem.saidaMaterial.responsavel.pessoa.nome
    dataSaida = itemMovimentoAlmoxarifado.saidaMaterial.dataSaida.formatar("yyyy-MM-dd h:m:s")
    dataMovi = itemMovimentoAlmoxarifado.dataHoraMovimento.formatar("yyyy-MM-dd h:m:s")
    almoxarifado = itemMovimentoAlmoxarifado.almoxarifado.descricao
    //organograma = itemMovimentoAlmoxarifado.organograma.numero
    organograma = itemMovimentoAlmoxarifado.organograma.descricao
    organogramaNumero = itemMovimentoAlmoxarifado.organograma.numero
    responsavel = itemMovimentoAlmoxarifado.saidaMaterial.almoxarifado.responsavel.pessoa.nome
    natureza = itemMovimentoAlmoxarifado.saidaMaterial.naturezaMovimentacao.tipo
    totalItem = quantidade * valorUnitario
    valorTotal = itemMovimentoAlmoxarifado.valorTotalMovimento
    
    
    chave = [
      entidadeId: itemMovimentoAlmoxarifado.entidade.id,
      saidaNumero: itemMovimentoAlmoxarifado.saidaMaterial.id,
      idSaidaItem: itemMovimentoAlmoxarifado.saidaMaterialItem.id,
      almoxarifado: itemMovimentoAlmoxarifado.almoxarifado.id,
      organograma: itemMovimentoAlmoxarifado.organograma.id,
      
    ]
    // imprimir "Chave: $chave"
    
    fonte.inserirLinha([
      ordem: ordem.toString(),
      descricaoItem: descricaoItem,
      quantidade: quantidade,
      valorUnitario: valorUnitario,
      unidadeMedida: unidadeMedida,
      valorTotal: valorTotal,
      totalItem: totalItem,
      responsavelRetirada: responsavelRetirada,
      entidadeEndereco: itemMovimentoAlmoxarifado.entidade.endereco,
      entidadeNumero: itemMovimentoAlmoxarifado.entidade.numero,
      entidadeBairro: itemMovimentoAlmoxarifado.entidade.bairro,
      entidadeCep: itemMovimentoAlmoxarifado.entidade.cep,
      entidadeCnpj: itemMovimentoAlmoxarifado.entidade.cnpj,
      entidadeTelefone: itemMovimentoAlmoxarifado.entidade.telefone,
      entidadeEmail: itemMovimentoAlmoxarifado.entidade.email,
      entidadeSite: itemMovimentoAlmoxarifado.entidade.site,
      saidaNumero: itemMovimentoAlmoxarifado.saidaMaterial.numeroSaida,
      dataSaida: itemMovimentoAlmoxarifado.saidaMaterial.dataSaida.formatar('dd-MM-yyyy h:m:s').toString(),
      natureza: itemMovimentoAlmoxarifado.saidaMaterial.naturezaMovimentacao.tipo,
      almoxarifado: itemMovimentoAlmoxarifado.almoxarifado.descricao,
      //organograma: itemMovimentoAlmoxarifado.organograma.numero,
      organograma: itemMovimentoAlmoxarifado.organograma.descricao,
      organogramaNumero: itemMovimentoAlmoxarifado.organograma.numero,
      responsavel: itemMovimentoAlmoxarifado.saidaMaterial.almoxarifado.responsavel.pessoa.nome,
    ])
    imprimir "Quantidade: ${quantidade} Valor Unitario: ${valorUnitario} Valor Total: ${valorTotal}"
  }
}
retornar fonte
