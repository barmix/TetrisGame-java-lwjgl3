# version 400 core

in vec3 position;

uniform vec3 pos;

void main(void){

	gl_Position = vec4(position + pos, 1.0);

}
