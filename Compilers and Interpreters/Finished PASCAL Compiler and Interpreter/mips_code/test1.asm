	.text
	.globl main
	
main:
	la $t0, varignore
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	subu $sp, $sp, 4
	sw $ra, ($sp) # PUSH
	jal procfoo
	lw $ra, ($sp)
	addu $sp, $sp, 4 # POP
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	
	li $v0 10
	syscall	# halt
	
procfoo:
	li $t0, 0
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	la $t0, varignore
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	subu $sp, $sp, 4
	sw $ra, ($sp) # PUSH
	jal procbar
	lw $ra, ($sp)
	addu $sp, $sp, 4 # POP
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	li $v0, 0
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	li $v0, 3
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	# now num1 in $t0 and num2 in $v0
	subu $v0, $t0, $v0 # adds and stores in $v0
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	move $v0, $t0
	jr $ra # end of procfoo
procbar:
	li $t0, 0
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	li $v0, 3
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	move $v0, $t0
	jr $ra # end of procbar
	
	.data
	
varignore:
	.word 0
nextln:
	.asciiz "\n"
