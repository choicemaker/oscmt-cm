/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.compiler.parser;

import com.choicemaker.cm.compiler.Tree;
import com.choicemaker.cm.compiler.Tree.Apply;
import com.choicemaker.cm.compiler.Tree.ArrayType;
import com.choicemaker.cm.compiler.Tree.Bad;
import com.choicemaker.cm.compiler.Tree.Binop;
import com.choicemaker.cm.compiler.Tree.ClueDecl;
import com.choicemaker.cm.compiler.Tree.ClueSetDecl;
import com.choicemaker.cm.compiler.Tree.Ident;
import com.choicemaker.cm.compiler.Tree.If;
import com.choicemaker.cm.compiler.Tree.ImportDecl;
import com.choicemaker.cm.compiler.Tree.Index;
import com.choicemaker.cm.compiler.Tree.Indexed;
import com.choicemaker.cm.compiler.Tree.Let;
import com.choicemaker.cm.compiler.Tree.Literal;
import com.choicemaker.cm.compiler.Tree.MethodDecl;
import com.choicemaker.cm.compiler.Tree.New;
import com.choicemaker.cm.compiler.Tree.NewArray;
import com.choicemaker.cm.compiler.Tree.PackageDecl;
import com.choicemaker.cm.compiler.Tree.PrimitiveType;
import com.choicemaker.cm.compiler.Tree.Quantified;
import com.choicemaker.cm.compiler.Tree.Select;
import com.choicemaker.cm.compiler.Tree.Self;
import com.choicemaker.cm.compiler.Tree.Shorthand;
import com.choicemaker.cm.compiler.Tree.Typeop;
import com.choicemaker.cm.compiler.Tree.Unop;
import com.choicemaker.cm.compiler.Tree.Valid;
import com.choicemaker.cm.compiler.Tree.VarDecl;
import com.choicemaker.cm.compiler.Tree.Visitor;
import com.choicemaker.cm.core.compiler.CompilerException;

/**
 * A framework for visitors with default clauses
 *
 * @author   Matthias Zenger
 */
public abstract class DefaultVisitor implements Visitor {

	/** the default case
	 */
	public abstract void visit(Tree t) throws CompilerException;

	// map all other cases to the default case

	@Override
	public void visit(Bad t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(PackageDecl t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(ImportDecl t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(ClueSetDecl t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(ClueDecl t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Index t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(MethodDecl t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(VarDecl t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Quantified t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Let t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Shorthand t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Valid t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(If t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Apply t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(New t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(NewArray t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Typeop t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Unop t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Binop t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Indexed t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Select t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Ident t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Self t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(ArrayType t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(PrimitiveType t) throws CompilerException {
		visit((Tree) t);
	}

	@Override
	public void visit(Literal t) throws CompilerException {
		visit((Tree) t);
	}
}
