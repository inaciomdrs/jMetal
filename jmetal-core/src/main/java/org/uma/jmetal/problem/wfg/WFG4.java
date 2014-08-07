//  WFG4.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.problem.wfg;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.JMetalException;

/**
 * This class implements the WFG4 problem
 * Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston
 * A Scalable Multi-objective Test Problem Toolkit.
 * Evolutionary Multi-Criterion Optimization:
 * Third International Conference, EMO 2005.
 * Proceedings, volume 3410 of Lecture Notes in Computer Science
 */
public class WFG4 extends WFG {
  private static final long serialVersionUID = -5316522577489186702L;

  /**
   * Creates a default WFG4 with
   * 2 position-related parameter,
   * 4 distance-related parameter and
   * 2 objectives
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public WFG4(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 2, 4, 2);
  } // WFG4

  /**
   * Creates a WFG4 problem instance
   *
   * @param k            Number of position parameters
   * @param l            Number of distance parameters
   * @param M            Number of objective functions
   * @param solutionType The solutiontype type must "Real" or "BinaryReal".
   */
  public WFG4(String solutionType, Integer k, Integer l, Integer M)
    throws ClassNotFoundException, JMetalException {
    super(solutionType, k, l, M);
    problemName = "WFG4";

    s = new int[m];
    for (int i = 0; i < m; i++) {
      s[i] = 2 * (i + 1);
    }

    a = new int[m - 1];
    for (int i = 0; i < m - 1; i++) {
      a[i] = 1;
    }
  }

  /** Evaluate() method */
  public float[] evaluate(float[] z) {
    float[] y;

    y = normalise(z);
    y = t1(y, k);
    y = t2(y, k, m);

    float[] result = new float[m];
    float[] x = calculate_x(y);
    for (int m = 1; m <= this.m; m++) {
      result[m - 1] = d * x[this.m - 1] + s[m - 1] * (new Shapes()).concave(x, m);
    }

    return result;
  }

  /**
   * WFG4 t1 transformation
   */
  public float[] t1(float[] z, int k) {
    float[] result = new float[z.length];

    for (int i = 0; i < z.length; i++) {
      result[i] = (new Transformations()).s_multi(z[i], 30, 10, (float) 0.35);
    }

    return result;
  }

  /**
   * WFG4 t2 transformation
   */
  public float[] t2(float[] z, int k, int M) {
    float[] result = new float[M];
    float[] w = new float[z.length];

    for (int i = 0; i < z.length; i++) {
      w[i] = (float) 1.0;
    }

    for (int i = 1; i <= M - 1; i++) {
      int head = (i - 1) * k / (M - 1) + 1;
      int tail = i * k / (M - 1);
      float[] subZ = subVector(z, head - 1, tail - 1);
      float[] subW = subVector(w, head - 1, tail - 1);

      result[i - 1] = (new Transformations()).r_sum(subZ, subW);
    }

    int head = k + 1;
    int tail = z.length;

    float[] subZ = subVector(z, head - 1, tail - 1);
    float[] subW = subVector(w, head - 1, tail - 1);
    result[M - 1] = (new Transformations()).r_sum(subZ, subW);

    return result;
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public final void evaluate(Solution solution) throws JMetalException {
    float[] variables = new float[this.getNumberOfVariables()];
    Variable[] dv = solution.getDecisionVariables();

    for (int i = 0; i < this.getNumberOfVariables(); i++) {
      variables[i] = (float) dv[i].getValue();
    }

    float[] sol = evaluate(variables);

    for (int i = 0; i < sol.length; i++) {
      solution.setObjective(i, sol[i]);
    }
  }
}