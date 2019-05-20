/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bottomsheetmenuexample;

import android.graphics.Path;
import android.graphics.drawable.shapes.PathShape;

/**
 * Wrapper around {@link PathShape}
 * that creates a shape with a triangular path (pointing up or down).
 */
public class TriangleShape extends PathShape
{
    private Path mTriangularPath;

    public TriangleShape(Path path, float stdWidth, float stdHeight)
    {
        super(path, stdWidth, stdHeight);
        mTriangularPath = path;
    }

    public static TriangleShape create(float width, float height, float phase)
    {
        Path triangularPath = new Path();
        phase = (float) Math.sin(Math.PI / 2. * phase); //for better "bird" rotation simulation
        float yEnds = height * (1- phase);
        float yCenter = height * phase;

            triangularPath.moveTo(0, yEnds);
            triangularPath.lineTo(width / 2, yCenter);
            triangularPath.lineTo(width, yEnds);

        return new TriangleShape(triangularPath, width, height);
    }

}
