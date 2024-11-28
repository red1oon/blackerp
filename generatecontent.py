import os
import re

def generate_project_summary(directory):
    output_file = "project_summary.md"
    with open(output_file, "w") as f:
        # Step 1: Generate directory structure
        f.write("# Project Structure\n\n")
        for root, dirs, files in os.walk(directory):
            level = root.replace(directory, "").count(os.sep)
            indent = "    " * level
            f.write(f"{indent}{os.path.basename(root)}/\n")
            sub_indent = "    " * (level + 1)
            for file in files:
                f.write(f"{sub_indent}{file}\n")

        f.write("\n# API and Class Summary\n\n")

        # Step 2: Extract class, method names, and parameters
        for root, _, files in os.walk(directory):
            for file in files:
                if file.endswith((".kt", ".java")):
                    file_path = os.path.join(root, file)
                    with open(file_path, "r") as code_file:
                        content = code_file.read()

                        # Extract package name
                        package_match = re.search(r'package\s+([\w\.]+);?', content)
                        package_name = package_match.group(1) if package_match else "No package"

                        # Extract classes
                        classes = re.findall(r'class\s+(\w+)', content)

                        if classes:
                            f.write(f"### File: {file}\n")
                            f.write(f"- **Package**: `{package_name}`\n")

                        # Extract methods within each class
                        for cls in classes:
                            f.write(f"- **Class**: `{cls}`\n")
                            
                            # Extract Kotlin and Java methods
                            methods = re.findall(
                                r'(?:fun|public|private|protected)\s+(\w+)\s*\(([^)]*)\)\s*(:\s*[\w<>\[\]]+)?',
                                content
                            )

                            for method_name, params, return_type in methods:
                                params_list = []
                                if params:
                                    params_list = [param.strip() for param in params.split(",")]
                                params_str = ", ".join(params_list)
                                return_str = return_type.strip() if return_type else "Unit"
                                f.write(f"  - **Method**: `{method_name}({params_str}) -> {return_str}`\n")

                            f.write("\n")
    
    print(f"Project summary saved to {output_file}")

if __name__ == "__main__":
    generate_project_summary("./src")  # Replace "./src" with your actual project directory
