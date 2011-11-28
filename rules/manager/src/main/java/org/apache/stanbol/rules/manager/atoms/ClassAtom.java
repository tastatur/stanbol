/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.stanbol.rules.manager.atoms;

import java.util.ArrayList;

import org.apache.stanbol.rules.base.api.SPARQLObject;
import org.apache.stanbol.rules.base.api.URIResource;
import org.apache.stanbol.rules.manager.SPARQLNot;
import org.apache.stanbol.rules.manager.SPARQLTriple;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.reasoner.rulesys.ClauseEntry;
import com.hp.hpl.jena.vocabulary.RDF;

import org.apache.stanbol.rules.base.SWRL;

public class ClassAtom extends CoreAtom {

	private URIResource classResource;
	private URIResource argument1;
	
	public ClassAtom(URIResource classResource, URIResource argument1) {
		this.classResource = classResource;
		this.argument1 = argument1;
	}
	
	@Override
	public SPARQLObject toSPARQL() {
		String argument1SPARQL = null;
		String argument2SPARQL = null;
		
		boolean negativeArg = false;
		boolean negativeClass = false;
		
		if(argument1.toString().startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			argument1SPARQL = "?"+argument1.toString().replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
			VariableAtom variable = (VariableAtom) argument1;
			negativeArg = variable.isNegative();
		}
		else{
			argument1SPARQL = argument1.toString();
		}
		
		if(classResource.toString().startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			argument2SPARQL = "?"+classResource.toString().replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
			VariableAtom variable = (VariableAtom) classResource;
			negativeClass = variable.isNegative();
		}
		else{
			argument2SPARQL = classResource.toString();
		}
		
	
		if(negativeArg || negativeClass){
			String optional = argument1SPARQL + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " + argument2SPARQL;
			
			ArrayList<String> filters = new ArrayList<String>();
			if(negativeArg){
				filters.add("!bound(" + argument1SPARQL + ")");
			}
			if(negativeClass){
				filters.add("!bound(" + argument2SPARQL + ")");
			}
			
			String[] filterArray = new String[filters.size()];
			filterArray = filters.toArray(filterArray);
			
			return new SPARQLNot(optional, filterArray);
		}
		else{
			return new SPARQLTriple(argument1SPARQL + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " + argument2SPARQL);
		}
	}

	@Override
	public Resource toSWRL(Model model) {
		
		
		Resource classAtom = model.createResource(SWRL.ClassAtom);
		
		Resource classPredicate = model.createResource(classResource.toString());
		
		classAtom.addProperty(SWRL.classPredicate, classPredicate);
		
		Resource argumentResource = argument1.createJenaResource(model);
		
		classAtom.addProperty(SWRL.argument1, argumentResource);
		
		
		return classAtom;
	}
	
	public SWRLAtom toSWRL(OWLDataFactory factory) {
		
		OWLClass classPredicate = factory.getOWLClass(IRI.create(classResource.getURI().toString()));
		
		SWRLIArgument argumentResource;
		if(argument1 instanceof ResourceAtom){
			OWLIndividual owlIndividual = factory.getOWLNamedIndividual(IRI.create(argument1.getURI().toString()));
			argumentResource = factory.getSWRLIndividualArgument(owlIndividual);
		}
		else{
			argumentResource = factory.getSWRLVariable(IRI.create(argument1.getURI().toString()));
		}
		
		
		return factory.getSWRLClassAtom(classPredicate, argumentResource);
		
	}
	
	public URIResource getClassResource() {
		return classResource;
	}
	
	public URIResource getArgument1() {
		return argument1;
	}
	
	@Override
	public String toString() {
		
		return argument1.toString() + " is an individual of the class "+classResource.toString();
	}
	
	@Override
	public String toKReSSyntax(){
		String arg1 = null;
		String arg2 = null;
		
		if(argument1.toString().startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			arg1 = "?"+argument1.toString().replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
			VariableAtom variable = (VariableAtom) argument1;
			if(variable.isNegative()){
				arg1 = "notex(" + arg1 + ")";
			}
		}
		else{
			arg1 = argument1.toString();
		}
		
		if(classResource.toString().startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			arg2 = "?"+classResource.toString().replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
			VariableAtom variable = (VariableAtom) classResource;
			if(variable.isNegative()){
				arg2 = "notex(" + arg2 + ")";
			}
		}
		else{
			arg2 = classResource.toString();
		}
		return "is(" + arg2 + ", " + arg1 + ")";
	}

	@Override
	public boolean isSPARQLConstruct() {
		return false;
	}
	
	@Override
	public boolean isSPARQLDelete() {
		return false;
	}
	
	@Override
	public boolean isSPARQLDeleteData() {
		return false;
	}

	@Override
	public ClauseEntry toJenaClauseEntry() {
		String subject = argument1.toString();
		if(subject.startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			subject = subject.replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
		}
		
		String object = classResource.toString();
		if(object.startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			object = subject.replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
		}
		ClauseEntry clauseEntry = new TriplePattern(Node.createVariable(subject), RDF.type.asNode(), Node.createURI(object));
		return clauseEntry;
	}
}
