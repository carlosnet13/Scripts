enumerador = importar "enumerador";

esquema = [
 codigo: Esquema.inteiro, 
 areaDoLote : Esquema.numero,
 areaTotalConstruida: Esquema.numero
];

camposAdicionaisImovel = { pImovel, pAno ->
params =[idImovel: pImovel, ano: pAno];
 
retornoCamposAdicionais = Dados.tributos.v2.imovel.camposAdicionais.busca(parametros: params)
 
mapCamposAdicionais = [: ]
retornoCamposAdicionais.each{
  camposAdicionais ->
    chave = camposAdicionais.campoAdicional.titulo
  mapCamposAdicionais[chave] = camposAdicionais
}
retornar mapCamposAdicionais
}
 

fonte = Dados.dinamico.v2.novo(esquema);


idParam = parametros.Cdimovel?.valor;
anoCampo = parametros?.anoCampo?.valor ?: Datas.hoje().ano;
areaDoLote  = '';
areaTotalConstruida = '';

// Busca imoveis
imovel = Dados.tributos.v2.imoveis;

camposimovel = "id"

criterioimovel = "";
se (parametros.Cdimovel?.valor){
  criterioimovel = "codigo in ($idParam)";
};
// Percorre para buscar imovel 

percorrer(imovel.busca(campos:camposimovel,criterio:criterioimovel)){ imovel ->
  
  buscaCampo = camposAdicionaisImovel(imovel.id, 2022)
  
  
 	linha = [codigo: imovel.codigo, 
			 areaDoLote : buscaCampo['AREA DO LOTE']?.vlCampo ?: 0,
 			 areaTotalConstruida : buscaCampo['AREA TOTAL CONSTRUIDA']?.vlCampo ?: 0
             ]
  
	fonte.inserirLinha(linha); 
	imprimir linha
    imprimir anoCampo

  	//suspender()
}

retornar fonte;