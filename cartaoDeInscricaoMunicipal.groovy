enumerador 			= importar "enumerador";
formata    			= importar "mascara";
fonteEconomicos 	= Dados.tributos.v2.economicos
fonteAtividades 	= Dados.tributos.v2.economicos.atividades
fonteServicos		= Dados.tributos.v2.economico.servicos
fonteContribuintes  = Dados.tributos.v2.contribuintes

esquema = [
  listAtividades 	: Esquema.lista(Esquema.objeto([
    codigoCnae	    : Esquema.caracter,
    descricaoCnae	    : Esquema.caracter,
    principal : Esquema.caracter,
  ])),
  listServicos : Esquema.lista(Esquema.objeto([
    itemServic	: Esquema.caracter,
    descServic	: Esquema.caracter, 
  ])),
  listContribuinte : Esquema.lista(Esquema.objeto([
    insContribui	: Esquema.caracter,
  ])),
  codigoAtiv        : Esquema.caracter,
  descricaoAtiv     : Esquema.caracter,
  nome				: Esquema.caracter,
  cpfCnpj			: Esquema.caracter,
  nomeFantasia		: Esquema.caracter,
  codContribuinte   : Esquema.inteiro,
  codEconomico		: Esquema.inteiro,
  dtInicio			: Esquema.caracter,
  enderecoFormatado : Esquema.caracter,
  municipio         : Esquema.caracter,
  inscricaoEstadual : Esquema.caracter,
  requerimento		: Esquema.caracter,
  dtabertura		: Esquema.caracter,
  cep			    : Esquema.caracter,
  validade          : Esquema.caracter
]

fonte = Dados.dinamico.v2.novo(esquema)

principal 		= '';
tipoPessoa 		= '';
criteriocartao  = '';

parametroEcon  = parametros.economico.selecionado.valor
criteriocartao = "id = $parametroEcon";


listAtividades = [];
listServicos   = [];
listContribuinte = [];
filtroEconomicos = "codigo in " + parametroEcon

dadosEconomicos = fonteEconomicos.busca(criterio: criteriocartao)
imprimir "Criterio Cartão:  " + criteriocartao

for ( itemEconomicos in dadosEconomicos) {
  imprimir "$itemEconomicos"
  idEcon             = itemEconomicos.id
  id				 = itemEconomicos.contribuinte.id	
  nome               = itemEconomicos.contribuinte.nome
  cpfCnpj            = formata.formatar(itemEconomicos.contribuinte.cpfCnpj)
  codEconomico		 = itemEconomicos.codigo
  codContribuinte    = itemEconomicos.contribuinte.codigo
  dtabertura		 = itemEconomicos.dhInicioAtividade
  enderecoFormatado  = itemEconomicos.enderecoFormatado
  municipio          = itemEconomicos.municipio.nome
  nomeFantasia		 = itemEconomicos.nomeFantasia
  cep				 = itemEconomicos.cep
  
  fonteMovimentacoes = Dados.tributos.v2.economicos.movimentacoes;

  filtroMovimentacoes = "idEconomico = $idEcon"
  
  dadosMovimentacoes = fonteMovimentacoes.busca(criterio: filtroMovimentacoes,campos: "tipoMovimentacao, dhMovimentacao", ordenacao: "dhMovimentacao asc")
  
  dtInicio = nulo
  percorrer (dadosMovimentacoes) { item ->
    se (item.tipoMovimentacao.valor == 'SITUACAO') {
      dtInicio = item.dhMovimentacao
      parar()
    }
  }
  imprimir "data: " + dtInicio
  
  
  imprimir "Data da Abertura da fonte: " + dtabertura.formatar('dd-MM-yyyy') + "Nome: " + nome
  
  // Busca de campos adicionais da data da abertura
  
  filtroEconomicos = "id in ($parametroEcon)";
  
  lerDadoAdicionalEconomicodhCampo = { idEconomico,nomeCampo ->    
    imprimir "$idEconomico,$nomeCampo"
    valorCampo = dtabertura;                                                                   
    tentar {
      params  = [ idEconomico: idEconomico];  
      buscaCA = Dados.tributos.v2.economico.camposAdicionais.busca(criterio:criteriocartao,parametros: params)
      .find { c -> c.campoAdicional.titulo.equals(nomeCampo) };
        valorCampo =  buscaCA ? buscaCA.dhCampo : dtInicio ? dtInicio.formatar('dd/MM/yyyy') : dtabertura.formatar('dd/MM/yyyy');
      
    } tratar {
      imprimir "Erro na leitura dos campos adicionais";    
    }  
    imprimir "ValorCampo: $valorCampo"
    retornar valorCampo;
  }
  
  dadosEconomicos = fonteEconomicos.busca(criterio: filtroEconomicos)
  codImo 			= itemEconomicos.id
  dtabertura 		= lerDadoAdicionalEconomicodhCampo(codImo,"Data da Abertura");


  
  // dados contribuinte com inscrição estadual	
  
  filtroContribuintes   = "id = " + itemEconomicos.contribuinte.id
  dadosContribuintes    = fonteContribuintes.busca(criterio: filtroContribuintes)
  
  for (def itemContribuintes : dadosContribuintes) {
    tipoPessoa = enumerador.converter(itemContribuintes.tipoPessoa).valor ?: ""
    if(tipoPessoa == 'JURIDICA'){
      insCont	 = itemContribuintes.pessoaJuridica.inscricaoEstadual
    }
    listContribuinte   << [
      insContribui	 : itemContribuintes.pessoaJuridica.inscricaoEstadual
      
    ]
  }
  
  // dados atividades
  filtroAtividades   = "idEconomico = " + itemEconomicos.id
  dadosAtividades    = fonteAtividades.busca(criterio: filtroAtividades)
  
  for (def itemAtividades : dadosAtividades) {
    principal = enumerador.converter(itemAtividades.principal).valor ?: ""
    if(principal == 'SIM'){
      codigoAtiv        = itemAtividades.atividade.codigoCnae
      descricaoAtiv     = itemAtividades.atividade.descricao
    }
    listAtividades   << [
      codigoCnae	 : itemAtividades.atividade.codigoCnae,
      descricaoCnae	 : itemAtividades.atividade.descricao,
      principal : principal
    ]
  }
  
  imprimir codigoAtiv
  
  
  // dados servicos
  dadosServicos    = fonteServicos.busca(ordenacao:"principal desc,servicos.id asc", parametros:["idEconomico":itemEconomicos.id])
  
  for (def itemServicos : dadosServicos) {
    principal = enumerador.converter(itemServicos.principal).valor ?: ""
    if(principal == 'SIM'){
      codigoServ        = itemServicos.servico.itemLista
      descricaoServ     = itemServicos.servico.descricao
    }
    listServicos   << [
      itemServic	 : itemServicos.servico.itemLista,
      descServic	 : itemServicos.servico.descricao
    ]
  }
  linha = [
    nome              : nome,
    nomeFantasia	  : nomeFantasia,
    cpfCnpj           : cpfCnpj,
    codEconomico	  : codEconomico,
    codContribuinte   : codContribuinte,
    listAtividades    : listAtividades,
    listServicos	  : listServicos, 
    listContribuinte  : listContribuinte,
    codigoAtiv        : codigoAtiv,
    descricaoAtiv     : descricaoAtiv,
    enderecoFormatado : enderecoFormatado,
    municipio         : municipio,
    dtabertura		  : dtabertura.toString(),  
    cep				  : cep,
    validade          : '31/12/' + Datas.hoje().ano
  ]
  
  fonte.inserirLinha(linha)
  imprimir linha
  imprimir dtabertura
}


retornar fonte





